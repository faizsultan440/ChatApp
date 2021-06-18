package com.example.rbchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.rbchat.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocation extends AppCompatActivity {
    SupportMapFragment smf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
    getmylocation(getIntent().getDoubleExtra("long",-122.084),getIntent().getDoubleExtra("lat",37.4219983));



    }
    private void getmylocation(double Longitude,double Latitude) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        smf.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull GoogleMap googleMap) {

                LatLng latLng=new LatLng(Latitude,Longitude);
                MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(" I am Here");
                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));


            }
        });


    }


}