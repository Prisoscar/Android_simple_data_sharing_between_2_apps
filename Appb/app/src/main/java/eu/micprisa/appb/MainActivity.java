package eu.micprisa.appb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double lat;
    private double lng;
    private GoogleMap map;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //LatLng of marker send by app a
        LatLng latLng = new LatLng(lat, lng);
        //get address from LatLng
        try {
            this.address = new Geocoder(this, Locale.getDefault()).getFromLocation(this.lat, this.lng, 1).get(0).getAddressLine(0);
        }catch (IOException e){
            this.address = "IOException has occurred";
        }
        //settings for center camera o marker
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .build();
        //add marker on map
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("chosen position"));
        //set camera on marker
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //get device position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            googleMap.isMyLocationEnabled();
        }

    }
}