package com.example.intern05.meetup.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.intern05.meetup.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude=0;
    private double longitude=0;
    private String eventAddressLatitude="";
    private String eventAddressLongitude="";
    private String eventName;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Intent i=getIntent();
        eventName=i.getStringExtra(EventDetails.KEY_EVENT_NAME);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission for GPS not granted.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
            }
            return;
        }
        else {
            mMap.setMyLocationEnabled(true);
        }

        root = FirebaseDatabase.getInstance().getReference("Events").child(eventName).child("Location");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventAddressLatitude=dataSnapshot.child("Latitude").getValue(String.class);
                eventAddressLongitude=dataSnapshot.child("Longitude").getValue(String.class);
                latitude=Double.parseDouble(eventAddressLatitude);
                longitude=Double.parseDouble(eventAddressLongitude);
                LatLng coordinate = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate,15));
                mMap.addMarker(new MarkerOptions().position(coordinate).title("Your event is here."));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // Toast.makeText(LocationActivity.this, eventAddressLatitude, Toast.LENGTH_SHORT).show();




    }
}
