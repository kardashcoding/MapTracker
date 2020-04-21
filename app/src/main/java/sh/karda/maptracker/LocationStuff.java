package sh.karda.maptracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

class LocationStuff {
    private String TAG = "LocationStuff";

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
