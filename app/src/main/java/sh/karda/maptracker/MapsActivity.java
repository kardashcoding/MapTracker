package sh.karda.maptracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import sh.karda.maptracker.get.GetLocations;
import sh.karda.maptracker.put.Sender;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final String TAG = "MapsActivity";
    final static String url = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    LocationManager locationManager;
    MarkerOptions mo;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Button loadButton = findViewById(R.id.button_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetLocations getLocations = new GetLocations(mMap, getDeviceId());
                getLocations.execute();
                LatLngBounds myArea = new LatLngBounds(new LatLng(59.92, 10.61), new LatLng(59.94, 10.62));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(myArea, 200));
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(10, 60)).title("My position");
        requestPermissions(PERMISSIONS, PERMISSION_ALL);
        if (isLocationEnabled()){
            requestLocation();
        }else {
            showAlert();
        }
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle("Skru på lokasjon")
        .setMessage("Du må inn i innstillinger og skru på lokasjon");

        dialog.show();
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
        LatLng myLocation = new LatLng(10.6173371, 59.9316922);
        LatLngBounds myArea = new LatLngBounds(new LatLng(10.61, 59.92), new LatLng(10.62, 59.94));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    private String getDeviceId(){
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onLocationChanged(Location location) {

        String latitude, longitude, height, speed;
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        latitude = String.valueOf(myLocation.latitude);
        longitude = String.valueOf(myLocation.longitude);
        height = String.valueOf(location.getAltitude());
        speed  = String.valueOf(location.getSpeed());

        Sender sender = new Sender(url, getDeviceId(), latitude, longitude, height, speed);
        sender.execute();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
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
        assert provider != null;
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
    }

    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
