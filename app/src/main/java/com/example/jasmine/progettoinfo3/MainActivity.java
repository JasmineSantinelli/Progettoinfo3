package com.example.jasmine.progettoinfo3;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Main activity of the project, it allows you to upload an image and see its classification
 * Implements AsyncResponse
 */
public class MainActivity extends AppCompatActivity  implements AsyncResponse {


    /**
     * Folder in witch the photos taken within the app will be saved
     *
     * (default folder... /PROGETTOINFO3)
     *
     */
    private static final String IMAGE_DIRECTORY = "/PROGETTOINFO3";
    /**
     * Location of the user costantly updated
     */
    private Location location;
    /**
     * Latutude of the user
     */
    private double longitude = 0;
    /**
     * Longitude of the user
     */
    private double latitude = 0;
    /**
     * String in witch the photo will be saved
     */
    private String mCurrentPhotoPath;

    private Button btnUpload;
    private TextView text,text1,text2,text3,text4;
    private ImageView imageview;
    private int GALLERY = 1, CAMERA = 2;


    /**
     * Method automatically called by the OS on the creation of the activity, it's used to set all
     * the parameters needed, it a sort of a constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        mCurrentPhotoPath = null;
        //btn = (Button) findViewById(R.id.btn);
        btnUpload= (Button) findViewById(R.id.btn_upload);
        btnUpload.setBackgroundResource(R.color.colorAccent);
        imageview = (ImageView) findViewById(R.id.iv);
        text=(TextView) findViewById(R.id.txt);
        text1 = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView7);
        text4 = findViewById(R.id.textView8);
        FloatingActionButton floatButton = findViewById(R.id.floatingActionButton3);
        //SE PREMO IL BTN RICHIAMA IL METODO PER APRIRE LA FINESTRA DI DIALOGO
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });*/
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        // Aggiorna posizione
        aggiornaPosix();

    }

    /**
     * Method invocated when pressed the button "aggiorna posizione",
     * The position is also updated constantly when the user moves
     * @param view
     */
    public void button_aggiornaPosix(View view){
        aggiornaPosix();
    }

    /**
     * Update position reteived trough the sensors gpsLocation and netLocation
     */
    void aggiornaPosix(){
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {

            //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //mFusedLocationClient.getLastLocation();
            LocationListener mLocListener = new MyLocationListener();
            LocationListener mLocListener2 = new MyLocationListener();
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener2);

            //mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,mLocationListener,Looper.getMainLooper());

            //location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsEnabled && networkEnabled) {
               if (gps_loc.getAccuracy() > net_loc.getAccuracy()) {
                    location = net_loc;
                    text3.setBackgroundColor(Color.YELLOW);
                    text4.setBackgroundColor(Color.TRANSPARENT);
               }
                else {
                   location = gps_loc;
                   text3.setBackgroundColor(Color.TRANSPARENT);
                   text4.setBackgroundColor(Color.YELLOW);
               }
                longitude=location.getLongitude(); // E
                latitude=location.getLatitude(); // N
                text1.setText("LAT : " + Double.toString(latitude));
                text2.setText("LON : " + Double.toString(longitude));
                text3.setText("NET Accuracy : " + String.format("%.2f", net_loc.getAccuracy()));
                text4.setText("GPS Accuracy : " + String.format("%.2f", gps_loc.getAccuracy()));

                } else {
                if (gpsEnabled){
                    location = gps_loc;
                } else if (networkEnabled){
                    location = net_loc;
                } else {
                    Toast.makeText(this, "Posizione non trovata :(", Toast.LENGTH_SHORT).show();
                }
            }
            //Toast.makeText(this, "Posizione aggiornata", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e){
            Toast.makeText(this, "errore (per non dire bestemmie)" , Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Select between camera or gallery to upload an image
     */
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Cosa vuoi fare?");
        btnUpload.setClickable(true);
        btnUpload.setText("Scopri quanto fai cagare");
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

    /**
     * Select a photo from the phone gallery
     */
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    /**
     * Create a new file in witch the photo taken from the camera will be later saved
     * @return The empty file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        storageDir = wallpaperDirectory;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Toast.makeText(this, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
        return image;
    }

    /**
     * Take a foto from the camera, it call the createImageFile() function
     */
    private void takePhotoFromCamera() {
        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, CAMERA);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    /**
     * The result after the user select either select an image from the gallery or take a new picture,
     * the parameters are automatically set from the OS
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                    Bitmap bMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    mCurrentPhotoPath = saveImage(bMap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    //bMap = RotateBitmap(bMap,90);
                    imageview.setImageBitmap(bMap);
                    //mostro path per vedere se va bene
                    text.setText(mCurrentPhotoPath);
                    btnUpload.setVisibility(View.VISIBLE);
                    //btn.setText("Modifica inserimento");
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
           // Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            try {
                FileInputStream in = new FileInputStream(mCurrentPhotoPath);
                BufferedInputStream buf = new BufferedInputStream(in);
                byte[] bMapArray = new byte[buf.available()];
                buf.read(bMapArray);
                Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
                bMap = RotateBitmap(bMap,90);
                imageview.setImageBitmap(bMap);
            } catch (Exception e ){
                //Dioboia
            }
            btnUpload.setClickable(true);
            btnUpload.setBackgroundColor(Color.YELLOW);
            btnUpload.setVisibility(View.VISIBLE);
            btnUpload.setText("Scopri che dissesto sei");
            //btn.setText("Modifica inserimento");
            //path=saveImage(thumbnail);
            //mostro il path per vedere se è giusto*/
            text.setText(mCurrentPhotoPath);
            //Toast.makeText(MainActivity.this, "Image Saved!"+path, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Rotate a bitmap
     *
     * @param source The image to be rotated
     * @param angle The angle in deegres of the rotation (clockwise)
     * @return The rotated bitmap
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Save an image in a specified directory, the directory is a global parameter
     * @param myBitmap The bitmap to be saved
     * @return The absolute path of the image
     */
    private String saveImage(Bitmap myBitmap) {
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
        return "Error";
    }
    //require permission
    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)

                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                           // Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
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

    /**
     * Function that use HTTP connection to make a POST request to the server
     * @param view
     */
    public void upload(View view) {
        //execute task with parameters of path latitude and longitude
        try {
            if (mCurrentPhotoPath != null) {
                Upload asyncTask = new Upload();
                asyncTask.delegate = this;
                String lon = Double.toString(longitude);
                String lat = Double.toString(latitude);
                asyncTask.execute(mCurrentPhotoPath, lon, lat);
                btnUpload.setVisibility(View.VISIBLE);
                btnUpload.setClickable(false);
                btnUpload.setBackgroundColor(Color.GREEN);
                btnUpload.setText("Sto pensando merdaccia");
            } else {
                Toast.makeText(this, "Metti la foto pirlotto", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Mi sono rotto :(", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * It receives the result of the computation of the server
     * @param output The string received from the server
     */
    public void processFinish(String output){
         Toast.makeText(MainActivity.this, output, Toast.LENGTH_SHORT).show();
         String[] arr_output = output.split("-");

         text.setText(arr_output[1].toUpperCase() + " : " +arr_output[2] + "  ---  "
                 + arr_output[3].toUpperCase() + " : " +arr_output[4]);
         btnUpload.setText("Ho finito");
         btnUpload.setBackgroundColor(Color.CYAN);
         }

    /**
     * Open a new activity with a map
     * @param view
     */
    public void map(View view) {
        Intent intent=new Intent(this,MapsActivity.class);
        intent.putExtra("lat",latitude);
        intent.putExtra("lon",longitude);
        startActivity(intent);
    }

    /**
     * Anonymous class that re-define the LocationListener class, it update the position show in the
     * text view every time that the user moves
     */
    public class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
           // Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String arg0) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

}



