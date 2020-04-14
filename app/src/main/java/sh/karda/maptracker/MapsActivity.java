package sh.karda.maptracker;

import android.Manifest;
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
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.DbAsyncGetLastDay;
import sh.karda.maptracker.database.DbAsyncInsert;
import sh.karda.maptracker.database.DbManager;
import sh.karda.maptracker.get.GetLocations;
import sh.karda.maptracker.map.PopupAdapter;
import sh.karda.maptracker.put.Sender;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final String TAG = "MapsActivity";
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    LocationManager locationManager;
    private static Context context;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    FloatingActionButton fab, fab1, fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        fab = findViewById(R.id.floatingActionButton);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                animateFab();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Reloading positions", Toast.LENGTH_SHORT).show();
                animateFab();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        MapsActivity.context = getApplicationContext();
        mapFragment.getMapAsync(this);
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, true);

        initiateLocationManager();
        if (requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isOpen = false;

        }else{
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isOpen = true;
        }
    }

    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_maps),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                requestLocation();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_maps),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mMap == null) return;
        if (!PreferenceHelper.getDownloadAutomatically()){
            GetLocations getLocations = new GetLocations(mMap, getDeviceId());
            getLocations.execute();
        }
        DbAsyncGetLastDay asyncGetLastDay = new DbAsyncGetLastDay(mMap);
        asyncGetLastDay.execute();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        GetLocations getLocations = new GetLocations(mMap, getDeviceId());
        getLocations.execute();
    }

    public static String getDeviceId(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    @Override
    public void onLocationChanged(Location location) {
        TextView speedText = findViewById(R.id.text_speed);
        TextView accuracyText = findViewById(R.id.text_accuracy);
        speedText.setText("Speed: " + (int) (location.getSpeed()* 3.6));
        accuracyText.setText("Accuracy: " + (int) location.getAccuracy());
        if (location.getAccuracy() > 150) return;
        DbAsyncInsert threadHelper = new DbAsyncInsert(DbManager.getDbInstance(), location, getDeviceId(), isNetworkAvailable(getApplicationContext()), wifiName(), mMap);
        threadHelper.execute();
        if (!PreferenceHelper.getSyncOnlyOnWifi() && !isOnline(getApplicationContext())) return;
            Sender sender = new Sender("SEND");
            sender.execute();
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.isActiveNetworkMetered();
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


    public void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(PreferenceHelper.getAccuracyFromPreferences());
        criteria.setPowerRequirement(PreferenceHelper.getPowerFromPreferences());
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert provider != null;
        locationManager.requestLocationUpdates(provider, PreferenceHelper.getSecondsFromPreferences(), PreferenceHelper.getDistanceFromPreferences(), this);
    }

    public static Context getAppContext() {
        return MapsActivity.context;
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
        requestLocation();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("key_power")) {
                requestLocation();
                Toast.makeText(getApplicationContext(), "Power satt til " + sharedPreferences.getString("key_power", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_accuracy")) {
                requestLocation();
                Toast.makeText(getApplicationContext(), "Accuracy satt til " + sharedPreferences.getString("key_accuracy", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_seconds")) {
                requestLocation();
                Toast.makeText(getApplicationContext(), "Seconds satt til " + sharedPreferences.getString("key_seconds", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_distance")) {
                requestLocation();
                Toast.makeText(getApplicationContext(), "Distance satt til " + sharedPreferences.getString("key_distance", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
