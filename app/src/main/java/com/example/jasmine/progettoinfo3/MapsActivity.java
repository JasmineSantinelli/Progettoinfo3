package com.example.jasmine.progettoinfo3;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.GoogleMap.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,AsyncResponse,OnCameraIdleListener {

    private GoogleMap mMap;
    double latitudine;
    double longitudine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        latitudine=getIntent().getDoubleExtra("lat",0);
        longitudine=getIntent().getDoubleExtra("lon",0);
        //Toast.makeText(this, Double.toString(latitudine)+Double.toString(longitudine), Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void processFinish(String output) {
        String[] stringa=output.split("-");
        String[] elemento;
        if (stringa.length!=0){
            for (String aStringa : stringa) {
                // Toast.makeText(this, aStringa, Toast.LENGTH_SHORT).show();
                elemento = aStringa.split(",");
                if (elemento.length == 3 && !(elemento[1]).equals("None") && !(elemento[2]).equals("None")) {
                    LatLng x = new LatLng(Double.valueOf(elemento[1]), Double.valueOf(elemento[2]));
                    switch (elemento[0]){
                        case "buca":
                            mMap.addMarker(new MarkerOptions().position(x).title("Io sono una " + elemento[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            break;
                        case "fessura":
                            mMap.addMarker(new MarkerOptions().position(x).title("Io sono una " + elemento[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                            break;
                        case "rappezzo":
                            mMap.addMarker(new MarkerOptions().position(x).title("Io sono un " + elemento[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            break;
                        case "tombino":
                            mMap.addMarker(new MarkerOptions().position(x).title("Io sono un " + elemento[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            break;
                    }

                }
            }
    }
        else
        Toast.makeText(this, "Stringa vuota", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        LatLng io = new LatLng(latitudine,longitudine);
        mMap.addMarker(new MarkerOptions().position(io).title("Io sono qui!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(io,13));
    }


    public void setParameterToRequest(){
        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        String lon=Double.toString(longitudine);
        String lat=Double.toString(latitudine);
        String upN = Double.toString(curScreen.northeast.latitude);
        String upE = Double.toString(curScreen.northeast.longitude);
        String downN = Double.toString(curScreen.southwest.latitude);
        String downE = Double.toString(curScreen.southwest.longitude);
        //Toast.makeText(this,upN+"    "+upE+"     "+downN+"    "+downE, Toast.LENGTH_LONG).show();
        RequestForMap asyncTask = new RequestForMap();
        asyncTask.delegate = this;
        asyncTask.execute(lon,lat,upN,upE,downN,downE);
    }
//back to main
    public void back(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onCameraIdle() {
        setParameterToRequest();
    }
}
