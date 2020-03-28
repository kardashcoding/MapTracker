package sh.karda.maptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.room.Room;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.put.Sender;

public class GotWifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            if (!wifiName(context).equals("")){
                AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "production")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                Sender sender = new Sender(db);
                sender.execute();
            }
        }
    }

    public String wifiName(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (wifiManager == null) return "";
        info = wifiManager.getConnectionInfo ();
        return info.getSSID();
    }
}
