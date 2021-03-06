package sh.karda.maptracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.BuildConfig;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import sh.karda.maptracker.database.DbActivity;
import sh.karda.maptracker.database.DbAsyncGetLastDay;
import sh.karda.maptracker.get.GetLocations;
import sh.karda.maptracker.map.MapHelper;
import sh.karda.maptracker.map.PopupAdapter;
import sh.karda.maptracker.put.Sender;

import static sh.karda.maptracker.LocationService.CHANNEL_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private static Context context;
    final String TAG = "MapsActivity";
    private static GoogleMap mMap;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    FloatingActionButton fabMain, fabProperties, fabRefresh, fabStart, fabCloud;
    NumberPicker numberPicker;
    boolean hasStarted = false;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;
    private LocationService locationService = null;
    private boolean bound;
    MyReceiver myReceiver;
    TextView speedText, accuracyText;
    Location previousLocation;

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiStateExtra == WifiManager.WIFI_STATE_ENABLED){
                Toast.makeText(MapsActivity.this, "Got Wifi", Toast.LENGTH_SHORT).show();
                Variables.setLastUsedMinSeconds(PreferenceHelper.getSecondsFromPreferences(context));
                Variables.setLastUsedMinDistance(PreferenceHelper.getSecondsFromPreferences(context));
                updateSettings(30, 100);
            }
            if (wifiStateExtra == WifiManager.WIFI_STATE_DISABLED){
                Toast.makeText(MapsActivity.this, "Lost Wifi", Toast.LENGTH_SHORT).show();
                updateSettings(Variables.getLastUsedMinSeconds(), Variables.getLastUsedMinDistance());
            }
        }

        private void updateSettings(int seconds, int distance) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            Map<String, ?> all = settings.getAll();
            editor.putString("key_min_seconds", String.valueOf(seconds));
            editor.apply();
            editor.putString("key_min_distance", String.valueOf(distance));
            editor.apply();
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            bound = false;
        }
    };

    public static Context getAppContext() {
        return context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
        myReceiver = new MyReceiver();
        createNotificationChannel();
        setContentView(R.layout.activity_maps);
        context = getApplicationContext();
        initializeControls();
        initiatePreferences(getApplicationContext());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        if (locationService != null) locationService.requestLocationUpdates();
        if (mMap == null) return;

        setButtonsState(requestingLocationUpdates(this));
        context = getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationService.ACTION_BROADCAST));
        if (mMap != null){
            DbAsyncGetLastDay asyncGetLastDay = new DbAsyncGetLastDay(mMap);
            asyncGetLastDay.execute();
        }
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }
    @Override
    protected void onStop() {
        if (bound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(serviceConnection);
            bound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        numberPicker.setValue(3);
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

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "eksempel", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        assert manager != null;
        manager.createNotificationChannel(serviceChannel);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_REQUESTING_LOCATION_UPDATES)){
            setButtonsState(sharedPreferences.getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false));
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
            if (location != null) {
                MapHelper.addSingleLocationToMap(mMap, location);
                if (previousLocation != null) MapHelper.drawPolyline(mMap, previousLocation.getLatitude(), previousLocation.getLongitude(), location.getLatitude(), location.getLongitude(), location.getSpeed());
                previousLocation = location;
                speedText.setText(speedText(location.getSpeed()));
                accuracyText.setText(accuracyText(location.getAccuracy()));
                LocationStuff.storeLocation(location, getApplicationContext());
            }
        }

        private String speedText(float speed){
            try {
                return getString(R.string.map_speed_text) + (int) (speed* 3.6);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        private String accuracyText(float accuracy){
            try {
                return getString(R.string.accuracy_text) + (int) accuracy;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
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
                mMap.setMyLocationEnabled(true);
                locationService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_maps),
                        R.string.permission_warning,
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

    private void initiatePreferences(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Variables.setLastUsedMinDistance(PreferenceHelper.getDistanceFromPreferences(context));
        Variables.setLastUsedMinSeconds(PreferenceHelper.getSecondsFromPreferences(context));
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (locationService == null) return;
            locationService.updateLocationSettings();

            if (!PreferenceHelper.getToastFromPreferences()) return;
            if (key.equals("key_power")) {
                Toast.makeText(getApplicationContext(), "Power set to " + sharedPreferences.getString("key_power", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_accuracy")) {
                Toast.makeText(getApplicationContext(), "Accuracy set to " + sharedPreferences.getString("key_accuracy", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_min_seconds")) {
                String seconds = sharedPreferences.getString("key_min_seconds", "7");
                Variables.setLastUsedMinSeconds(Integer.parseInt(seconds));
                Toast.makeText(getApplicationContext(), "Seconds set to " + sharedPreferences.getString("key_min_seconds", "<shit føkk>"), Toast.LENGTH_SHORT).show();
            }
            if (key.equals("key_min_distance")) {
                Toast.makeText(getApplicationContext(), "Distance set to " + sharedPreferences.getString("key_min_distance", "<shit føkk>"), Toast.LENGTH_SHORT).show();
                String seconds = sharedPreferences.getString("key_min_distance", "8");
                Variables.setLastUsedMinDistance(Integer.parseInt(seconds));
            }
        }
    };

    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            fabStart.setImageResource(R.drawable.ic_pause);
            hasStarted = true;
        } else {
            fabStart.setImageResource(R.drawable.ic_start);
            hasStarted = false;
        }
    }

    private void initializeControls() {
        speedText = findViewById(R.id.text_speed);
        accuracyText = findViewById(R.id.text_accuracy);
        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setDisplayedValues(Variables.numberPickerValues);
        NumberPicker.OnValueChangeListener numberPickerListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Toast.makeText(getApplicationContext(), Variables.numberPickerValues[picker.getValue() - 1], Toast.LENGTH_SHORT).show();
                Variables.setFromTime(Variables.numberPickerValues[picker.getValue() - 1]);
                DbAsyncGetLastDay asyncGetLastDay = new DbAsyncGetLastDay(mMap);
                asyncGetLastDay.execute();
            }
        };
        numberPicker.setOnValueChangedListener(numberPickerListener);
        numberPicker.setSoundEffectsEnabled(true);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);

        fabMain = findViewById(R.id.fabMain);
        fabProperties = findViewById(R.id.fabProperties);
        fabRefresh = findViewById(R.id.fabRefresh);
        fabCloud = findViewById(R.id.fabCloud);
        fabStart = findViewById(R.id.fab_start);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        fabMain.setTooltipText("Klikk her for faen!");
        fabProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    locationService.requestLocationUpdates();
                }
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                animateFab();
            }
        });
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbAsyncGetLastDay getLastDay = new DbAsyncGetLastDay(mMap);
                getLastDay.execute();
                animateFab();
            }
        });

        fabRefresh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DbActivity.class);
                startActivity(intent);
                return false;
            }
        });

        fabCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLocations getLocations = new GetLocations(mMap, LocationStuff.getDeviceId(getApplicationContext()));
                getLocations.execute();
                animateFab();
            }
        });

        if (PreferenceHelper.getUseCloudWifi()){
            fabCloud.setEnabled(false);
        }else{
            fabCloud.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Sender sender = new Sender("SEND", LocationStuff.getDeviceId(getApplicationContext()));
                    sender.execute();
                    animateFab();
                    Toast.makeText(getApplicationContext(), "Sending locations to cloud", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }


        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasStarted){
                    locationService.removeLocationUpdates();
                    setButtonsState(false);
                    animateFab();
                    Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
                    speedText.setText("");
                    accuracyText.setText("");
                }else{
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        locationService.requestLocationUpdates();
                        setButtonsState(true);
                        animateFab();
                        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void animateFab(){
        if (isOpen){
            fabMain.startAnimation(rotateForward);
            fabProperties.startAnimation(fabClose);
            fabRefresh.startAnimation(fabClose);
            fabStart.startAnimation(fabClose);
            fabCloud.startAnimation(fabClose);
            fabProperties.setClickable(false);
            fabRefresh.setClickable(false);
            fabCloud.setClickable(false);
            fabStart.setClickable(false);
            isOpen = false;

        }else{
            fabMain.startAnimation(rotateBackward);
            fabProperties.startAnimation(fabOpen);
            fabRefresh.startAnimation(fabOpen);
            fabCloud.startAnimation(fabOpen);
            fabStart.startAnimation(fabOpen);
            fabProperties.setClickable(true);
            fabRefresh.setClickable(true);
            fabCloud.setClickable(true);
            fabStart.setClickable(true);
            isOpen = true;
        }
    }
}
