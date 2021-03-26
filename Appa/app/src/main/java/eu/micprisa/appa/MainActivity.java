package eu.micprisa.appa;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView positionAddress;
    private boolean isPositionSet;
    private Marker marker;
    private final int RESULT_CODE = 12345;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //here will be sent data to App b via button
    public void sendDataToOtherApp (View view){

        if(this.isPositionSet) {
            Intent getAddress = new Intent();
            getAddress.setClassName("eu.micprisa.appb", "eu.micprisa.appb.MainActivity");
            getAddress.putExtra("lat", this.marker.getPosition().latitude);
            getAddress.putExtra("lng", this.marker.getPosition().longitude);
            startActivityForResult(getAddress, this.RESULT_CODE);
        } else{
            Toast.makeText(MainActivity.this, "Please chose a position on map.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        this.positionAddress= findViewById(R.id.positionAddress);
        if(requestCode == this.RESULT_CODE){
            if(resultCode == RESULT_OK){
                String positionAddress = data.getStringExtra("positionAddress");
                if(positionAddress != null){
                    this.positionAddress.setText(positionAddress);
                }else {
                    this.positionAddress.setText("Data not received");
                }
            }
            if(resultCode == RESULT_CANCELED){
                this.positionAddress.setText("Data not processed");
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.isPositionSet = false;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(MainActivity.this.isPositionSet) {
                    MainActivity.this.marker.remove();
                }
                MainActivity.this.isPositionSet = true;
                MainActivity.this.marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        //.draggable(true)
                        .title("chosen position"));
            }
        });

    }
}