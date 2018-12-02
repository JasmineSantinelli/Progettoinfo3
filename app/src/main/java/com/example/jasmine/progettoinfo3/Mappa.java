package com.example.jasmine.progettoinfo3;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
*/
public class Mappa extends FragmentActivity {
    //devo passargli i parametri che calcolo nel main della posizione
    //NB:quando li passo per mandarli al server li converto in stringa, qui li accetta come DOUBLE!

    Double latitudine,longitudine;
    //private final LatLng CENTER_POINT=new LatLng(latitudine, longitudine);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa);/*
        //get map non esiste più, bisogna capire come risolverlo (esiste getMapAsync ed è un puttanaio)
        GoogleMap map=((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        //move camera si sposta sulla posizione corrente
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER_POINT, 5));
       //ORA: bisogna fare un tast che richiami il server e richieda di mandare tutte le coppie di posizioni
        //quando vengono ricevute aggiungo in un ciclo tutti i marcatori alla mappa con il metodo seguente
        //ora sto aggiungendo un marker alla mia posizione
        map.addMarker(new MarkerOptions().position(CENTER_POINT));*/
     
    }
}
