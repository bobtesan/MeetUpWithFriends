package com.example.intern05.meetup.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.intern05.meetup.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private double latitude = 0;
    private double longitude = 0;
    private Button saveLocation;
    private String eventAddress;
    public static final String KEY_EVENT_ADDRESS = "eventAddress";
    public static final String KEY_EVENT_LATITUDE = "eventAddressLatitude";
    public static final String KEY_EVENT_LONGITUDE = "eventAddressLongitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        saveLocation = (Button) findViewById(R.id.button5);


        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    if (latitude == 0 && longitude == 0) {
                        Toast.makeText(MapsActivity.this, "Please place a marker on the map!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(MapsActivity.this, EventCreateActivity.class);
                        i.putExtra(MapsActivity.KEY_EVENT_ADDRESS, getEventAddress());
                        i.putExtra(MapsActivity.KEY_EVENT_LATITUDE, String.valueOf(getLatitude()));
                        i.putExtra(MapsActivity.KEY_EVENT_LONGITUDE, String.valueOf(getLongitude()));
                        startActivity(i);
                    }
                }
                else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder.setMessage("You don't have internet connection. Please connect to the internet.");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
            }
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                onMapLongClick(latLng);
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //Toast.makeText(getApplicationContext(), point.latitude + " " + point.longitude, Toast.LENGTH_SHORT).show();
        latitude = point.latitude;
        longitude = point.longitude;
        eventAddress = getAddress(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress(double lat, double lng) {
        String address = "";
        Geocoder gc = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = gc.getFromLocation(lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                Address adr = addressList.get(0);
                address = adr.getAddressLine(0);
                //Toast.makeText(this,address,Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
