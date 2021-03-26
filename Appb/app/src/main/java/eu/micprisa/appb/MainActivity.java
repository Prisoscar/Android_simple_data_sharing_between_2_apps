package eu.micprisa.appb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Locale;

//Gps location: https://www.youtube.com/watch?v=V62sxpyxapU&list=PLhPyEFL5u-i0vMDegCPbfD3ZNz69sf46I&index=1
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double lat = 0;
    private double lng = 0;
    private GoogleMap map;
    private String address;
    //From Google's location services imported in /src/build.gradle
    private FusedLocationProviderClient fusedLocationProviderClient;
    //Configuration object related to FusedLocationProviderClient
    private LocationRequest locationRequest;

    private static final int PERMISSION_FINE_LOCATION = 9934;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set all properties of LocationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        Intent getAddress = getIntent();
        this.lat = getAddress.getDoubleExtra("lat", 0);
        this.lng = getAddress.getDoubleExtra("lng",0);

        Button buttonSendAddress = findViewById(R.id.buttonSendAddress);

        buttonSendAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("positionAddress", MainActivity.this.address);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    //after permission will be granted this method will be triggered
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPosition();
                }else {
                    Toast.makeText(this,"this app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        //LatLng of marker send by app a
        LatLng latLng = new LatLng(lat, lng);
        //get address from LatLng
        try {
            this.address = new Geocoder(this, Locale.getDefault()).getFromLocation(this.lat, this.lng, 1).get(0).getAddressLine(0);
        }catch (IOException e){
            this.address = "IOException has occurred";
        }catch (IndexOutOfBoundsException e){
            this.address = "No address found";
        }
        //settings for center camera o marker
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .build();
        //add marker on map
        this.map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("chosen position"));
        //set camera on marker
        this.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //get device position
        getPosition();
    }

    private void getPosition() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check permissions to get Gps position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Here we'll get device position
                    try {
                        LatLng deviceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions()
                                .position(deviceLatLng)
                                .title("Your Position")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        );
                    }catch (NullPointerException e){
                        Toast.makeText(MainActivity.this, "Location is turned off.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //We will ask for permission to user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

            }
        }
    }

}