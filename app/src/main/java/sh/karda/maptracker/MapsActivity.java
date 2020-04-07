package sh.karda.maptracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.DatabaseHelper;
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
        serviceIntent = new Intent("sh.kardah.maptracker.LONGRUNSERVICE");
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

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        dialog.setTitle("Skru på lokasjon")
        .setMessage("Du må inn i innstillinger og skru på lokasjon");

        dialog.show();
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


    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public String wifiName(){
        WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
        WifiInfo info;
        if (wifiManager == null) return "";
        info = wifiManager.getConnectionInfo ();
        return info.getSSID();
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        }
        return false;
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
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_power", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_accuracy")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_accuracy", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_seconds")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_seconds", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_distance")) {
                stopService(serviceIntent);
                startService(serviceIntent);
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_distance", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
