package sh.karda.maptracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.get.GetLocations;
import sh.karda.maptracker.map.PopupAdapter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final String TAG = "MapsActivity";
    final static String url = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    LocationManager locationManager;
    AppDatabase db;
    private static Context context;
    Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        MapsActivity.context = getApplicationContext();
        mapFragment.getMapAsync(this);
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, true);
        initiateLocationManager();
        requestPermissions(PERMISSIONS, PERMISSION_ALL);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .build();
        serviceIntent = new Intent(getAppContext(), LocationTrackerService.class);
        serviceIntent.setAction("sh.karda.maptracker.LONGRUNSERVICE");
        startService(serviceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMap == null) return;
        GetLocations getLocations = new GetLocations(mMap, getDeviceId(), PreferenceHelper.getDrawLinesFromPreferences());
        getLocations.execute();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        GetLocations getLocations = new GetLocations(mMap, getDeviceId(), PreferenceHelper.getDrawLinesFromPreferences());
        getLocations.execute();
    }

    private String getDeviceId(){
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static Context getAppContext() {
        return MapsActivity.context;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    private void initiateLocationManager(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("key_power")) {
                stopService(serviceIntent);
                startForegroundService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_power", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_accuracy")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Accuracy satt til " + sharedPreferences.getString("key_accuracy", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_seconds")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Seconds satt til " + sharedPreferences.getString("key_seconds", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_distance")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Distance satt til " + sharedPreferences.getString("key_distance", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
