package sh.karda.maptracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import sh.karda.maptracker.database.DbAsyncInsert;
import sh.karda.maptracker.database.DbManager;
import sh.karda.maptracker.put.Sender;

import static android.content.Context.LOCATION_SERVICE;

class LocationStuff {
    private String TAG = "LocationStuff";
    private LocationManager locationManager;
    private Context context;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";


    LocationStuff(Context context){
        this.context = context;
        if (requestingLocationUpdates(context)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            TextView speedText = ((Activity)context).findViewById(R.id.text_speed);
            TextView accuracyText = ((Activity)context).findViewById(R.id.text_accuracy);
            speedText.setText("Speed: " + (int) (location.getSpeed()* 3.6));
            accuracyText.setText("Accuracy: " + (int) location.getAccuracy());
            if (location.getAccuracy() > 150) return;
            DbAsyncInsert threadHelper = new DbAsyncInsert(DbManager.getDbInstance(), location, getDeviceId(), isNetworkAvailable(context), wifiName(), null);
            threadHelper.execute();
            if (!PreferenceHelper.getSyncOnlyOnWifi() && !isOnline(context)) return;
            Sender sender = new Sender("SEND");
            sender.execute();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void requestLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(PreferenceHelper.getAccuracyFromPreferences());
        criteria.setPowerRequirement(PreferenceHelper.getPowerFromPreferences());
        String provider = locationManager.getBestProvider(criteria, true);

        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) return;
        assert provider != null;
        locationManager.requestLocationUpdates(provider, PreferenceHelper.getSecondsFromPreferences(), PreferenceHelper.getDistanceFromPreferences(), locationListener);
    }

    public void unRequestLocation(){
        locationManager.removeUpdates(locationListener);
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(((Activity)context),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    ((Activity)context).findViewById(R.id.activity_maps),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(((Activity)context),
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
            ActivityCompat.requestPermissions(((Activity)context),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    private String getDeviceId(){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String wifiName(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.isActiveNetworkMetered();
    }

}
