package sh.karda.maptracker;

import android.content.Context;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import sh.karda.maptracker.database.DbAsyncInsert;
import sh.karda.maptracker.database.DbManager;
import sh.karda.maptracker.put.Sender;

class LocationStuff {
    private static final String TAG = "LocationStuff";

    static void storeLocation(Location location, Context context){
        if (location.getAccuracy() < 40 && location.getAccuracy() > 0) {
            Log.v(TAG, "Storing location");
            DbAsyncInsert threadHelper = new DbAsyncInsert(DbManager.getDbInstance(), location, LocationStuff.getDeviceId(context), LocationStuff.isNetworkAvailable(context), LocationStuff.wifiName(context));
            threadHelper.execute();
            MediaPlayer player = MediaPlayer.create(context, R.raw.drip);
            player.start();
            if (PreferenceHelper.getSyncOnlyOnWifi() && !LocationStuff.isOnline(context)) return;
            Sender sender = new Sender("SEND", LocationStuff.getDeviceId(context));
            sender.execute();
        }
    }

    static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    static String wifiName(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info;
        if (wifiManager == null) return "";
        info = wifiManager.getConnectionInfo ();
        return info.getSSID();
    }

    static boolean isNetworkAvailable(Context context) {
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

    static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.isActiveNetworkMetered();
    }

}
