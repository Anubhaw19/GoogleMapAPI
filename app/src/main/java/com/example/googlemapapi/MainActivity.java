package com.example.googlemapapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback,
//add this classes:
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
     private GoogleMap mapAPI;
     private  GoogleApiClient googleApiClient;
     private LocationRequest locationRequest;
     private Location lastLocatoin;
     private Marker currentUserLocationMarker;
     private  static  final  int  Request_User_Location_Code=99;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapAPi);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI=googleMap;
//        LatLng jharkhand=new LatLng(23.331164, 85.298655);
//        mapAPI.addMarker(new MarkerOptions().position(jharkhand).title("i'm here"));
//        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(jharkhand));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            mapAPI.setMyLocationEnabled(true);
        }

    }
    //if user denied the permission previously then this block of code will execute:
    public  boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return  false;
        }
        else
        {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch ((requestCode))
        {
            case Request_User_Location_Code:
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                        mapAPI.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this," permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected  synchronized  void buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();


    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocatoin=location;
        if(currentUserLocationMarker !=null)
        {
            currentUserLocationMarker.remove();
        }
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        //noticable on MAP:
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Anubhaw found you😃");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentUserLocationMarker=mapAPI.addMarker(markerOptions);
         // focusing of CAMERA ,camera movement
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mapAPI.animateCamera(CameraUpdateFactory.zoomBy(12)); // zooming of map like 12,13,14 or other.

        if(googleApiClient!= null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
