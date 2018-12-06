package com.example.jasmine.progettoinfo3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private Location location = null;
    private Location location2 = null;
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
    private static int GALLERY = 1, CAMERA = 2;

    private static final String PREFS_NAME = "preferences";
    private static final String PREF_UNAME = "Username";
    private static final String PREF_PASSWORD = "Password";
    EditText name;
    private final String DefaultUnameValue = "";
    private String UnameValue;

    private final String DefaultPasswordValue = "";
    private String PasswordValue;

    private FusedLocationProviderClient mFusedLocationClient;



    private LocationManager mLocationManager;






    @Override
    public void onPause() {
        super.onPause();
        savePreferences();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(5000);
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
            //aggiornaPosix();
            // Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String arg0) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    /**
     * Method automatically called by the OS on the creation of the activity, it's used to set all
     * the parameters needed, it a sort of a constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onResume();
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCurrentPhotoPath = null;
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setBackgroundResource(R.color.colorAccent);
        imageview = findViewById(R.id.iv);
        text = findViewById(R.id.txt);
        text1 = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView7);
        text4 = findViewById(R.id.textView8);
        name = findViewById(R.id.editText);
        FloatingActionButton floatButton = findViewById(R.id.floatingActionButton3);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };



        try {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, null);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            LocationListener mLocListener = new MyLocationListener();
            LocationListener mLocListener2 = new MyLocationListener();
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener2);
        } catch (SecurityException e){}


        if (UnameValue.equals(""))
            UnameValue = null;
        if (UnameValue != null){
            name.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            text.setText("Bentornato " + UnameValue + "!");
        } else {
            text.setVisibility(View.GONE);
            name.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Inserisci il tuo nome e i dati della tua carta di credito", Toast.LENGTH_SHORT).show();
        }

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

                if (UnameValue == null || UnameValue.equals("")) {
                    UnameValue = name.getText().toString();
                }
                if (!UnameValue.equals("")){
                showPictureDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Inserisci un nome valido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Aggiorna posizione
        aggiornaPosix();
        aggiornaPosix2();

    }



    public void aggiornaPosix2(){
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (location != null) {
                                location2 = location;
                                Toast.makeText(MainActivity.this, "Posizione aggiornata",
                                        Toast.LENGTH_SHORT).show();
                                text.setText("accuratezza : " + location2.getAccuracy());
                                longitude=location2.getLongitude(); // E
                                latitude=location2.getLatitude(); // N
                                text1.setText("LA : " + Double.toString(latitude));
                                text2.setText("LN : " + Double.toString(longitude));

                            }
                        }
                    });

        } catch (SecurityException e){

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
     * Method invocated when pressed the button "aggiorna posizione",
     * The position is also updated constantly when the user moves
     * @param view
     */
    public void button_aggiornaPosix(View view){
        aggiornaPosix();
        aggiornaPosix2();
    }

    /**
     * Update position reteived trough the sensors gpsLocation and netLocation
     */
    void aggiornaPosix(){


        try {


            Location gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gpsEnabled && networkEnabled && gps_loc != null && net_loc != null) {
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
                Toast.makeText(this, "Nuova posizione settata", Toast.LENGTH_SHORT).show();
                } else if (gpsEnabled && gps_loc != null) {
                location = gps_loc;
                longitude=location.getLongitude(); // E
                latitude=location.getLatitude(); // N
                text1.setText("LAT : " + Double.toString(latitude));
                text2.setText("LON : " + Double.toString(longitude));
                text4.setText("GPS Accuracy : " + String.format("%.2f", gps_loc.getAccuracy()));

            } else if (networkEnabled && net_loc != null) {
                location = net_loc;
                longitude=location.getLongitude(); // E
                latitude=location.getLatitude(); // N
                text1.setText("LAT : " + Double.toString(latitude));
                text2.setText("LON : " + Double.toString(longitude));
                text3.setText("NET Accuracy : " + String.format("%.2f", net_loc.getAccuracy()));
            } else {
                throw new Exception("Non e possibile accedere ai sensori");
            }
            //Toast.makeText(this, "Posizione aggiornata", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e){
            Toast.makeText(this, "Dammi la posizione, ti voglio spiare" , Toast.LENGTH_SHORT).show();
        } catch (Exception e ){
            Toast.makeText(this, e.getMessage() , Toast.LENGTH_SHORT).show();
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
            if (mCurrentPhotoPath != null && UnameValue != null && !UnameValue.equals("")) {
                Upload asyncTask = new Upload("http://progettoscandurra.andreacavagna.it/caricacellulare");
                asyncTask.delegate = this;
                aggiornaPosix();
                aggiornaPosix2();
                String lon = Double.toString(longitude);
                String lat = Double.toString(latitude);
                name.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                asyncTask.execute(mCurrentPhotoPath, lon, lat, UnameValue);
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
         if (arr_output[0].equals("dissesto")) {
             text.setText(arr_output[1].toUpperCase() + " : " + arr_output[2] + "  ---  "
                     + arr_output[3].toUpperCase() + " : " + arr_output[4]);
         } else {
             text.setText("Non è stato rilevato un dissesto");
         }
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

    private void savePreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        System.out.println("onPause save name: " + UnameValue);
        editor.putString(PREF_UNAME, UnameValue);
        editor.commit();
    }

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get value
        UnameValue = settings.getString(PREF_UNAME, DefaultUnameValue);
        System.out.println("onResume load name: " + UnameValue);
    }



}



