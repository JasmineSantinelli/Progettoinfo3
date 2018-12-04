package com.example.jasmine.progettoinfo3;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements AsyncResponse {
    //Declaration
    private String path=null;
    private Button btnUpload;
    private Button btn;
    private TextView text;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/PROGETTOINFO3";
    private int GALLERY = 1, CAMERA = 2;
    Location location;
    double longitude = 0;
    double latitude = 0;

    //private FusedLocationProviderClient mFusedLocationClient;


    Upload asyncTask = new Upload();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        asyncTask.delegate = this;

        btn = (Button) findViewById(R.id.btn);
        btnUpload= (Button) findViewById(R.id.btn_upload);
        imageview = (ImageView) findViewById(R.id.iv);
        text=(TextView) findViewById(R.id.txt);
        //SE PREMO IL BTN RICHIAMA IL METODO PER APRIRE LA FINESTRA DI DIALOGO
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        //location

        aggiornaPosix();

    }

    public void button_aggiornaPosix(View view){
        aggiornaPosix();
    }

    void aggiornaPosix(){

        try {

            //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //mFusedLocationClient.getLastLocation();
            LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            //mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,mLocationListener,Looper.getMainLooper());

            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(networkEnabled){
                longitude=location.getLongitude(); // E
                latitude=location.getLatitude(); // N
            }
            text.setText("lon E: " + Double.toString(longitude) + " ||  lat N: " + Double.toString(latitude));
            Toast.makeText(this, "Posizione aggiornata", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e){

            text.setText("errore (per non dire bestemmie)");
        }
    }

    //select between camera or gallery to upload an image
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Cosa vuoi fare?");
        String[] pictureDialogItems = {
                "Seleziona una foto dalla galleria",
                "Scatta una foto" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    //select a photo from gallery
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    //capture a picture
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    //Activity results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    path = saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);
                    //mostro path per vedere se va bene
                    text.setText(path);
                    btnUpload.setVisibility(View.VISIBLE);
                    btn.setText("Modifica inserimento");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            btnUpload.setVisibility(View.VISIBLE);
            btn.setText("Modifica inserimento");
            path=saveImage(thumbnail);
            //mostro il path per vedere se è giusto
            text.setText(path);
            Toast.makeText(MainActivity.this, "Image Saved!"+path, Toast.LENGTH_SHORT).show();
        }
    }

    //save image in specified directory
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    //require permission
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        /**if (report.isAnyPermissionPermanentlyDenied()) {
                         // show alert dialog navigating to Settings
                         //openSettingsDialog();
                         }**/
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    //change Activity
    public void upload(View view) {
        //execute task with parameters of path latitude and longitude
        String lon=Double.toString(longitude);
        String lat=Double.toString(latitude);
        asyncTask.execute(path,lon,lat);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            aggiornaPosix();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

     public void processFinish(String output){
         Toast.makeText(MainActivity.this, output, Toast.LENGTH_SHORT).show();    }

    public void map(View view) {
        Intent intent=new Intent(this,MapsActivity.class);
        intent.putExtra("lat",latitude);
        intent.putExtra("long",longitude);
        startActivity(intent);
    }

    /*public class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String arg0) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }*/

}



