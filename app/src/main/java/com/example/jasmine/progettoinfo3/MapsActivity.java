package com.example.jasmine.progettoinfo3;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,AsyncResponse{

    private GoogleMap mMap;
    double latitudine=0;
    double longitudine=0;
    RequestForMap asyncTask = new RequestForMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        asyncTask.delegate = this;

        latitudine=getIntent().getDoubleExtra("lat",0);
        longitudine=getIntent().getDoubleExtra("lon",0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng io = new LatLng(latitudine,longitudine);
        mMap.addMarker(new MarkerOptions().position(io).title("SONO QUI!FUCK YEAH"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(io));

        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        Toast.makeText(this,"N" + curScreen.northeast.latitude + curScreen.northeast.longitude + "E" + curScreen.southwest.longitude+ curScreen.southwest.latitude, Toast.LENGTH_SHORT).show();
        String lon=Double.toString(longitudine);
        String lat=Double.toString(latitudine);
        String upN = Double.toString(curScreen.northeast.longitude);
        String upE = Double.toString(curScreen.northeast.latitude);
        String downN = Double.toString(curScreen.southwest.longitude);
        String downE = Double.toString(curScreen.northeast.latitude);
        asyncTask.execute(lon,lat,upN,upE,downN,downE);
    }

    public void back(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        String[] stringa=output.split("-");
        String[] elemento;
        for (int i=0;i<stringa.length;i++) {
            Toast.makeText(this, stringa[i], Toast.LENGTH_SHORT).show();

            elemento=stringa[i].split("|");
            LatLng x=new LatLng(Double.valueOf(elemento[1]),Double.valueOf(elemento[2]));
            mMap.addMarker(new MarkerOptions().position(x).title("io sono un" + elemento[0]));
        }
    }

}
