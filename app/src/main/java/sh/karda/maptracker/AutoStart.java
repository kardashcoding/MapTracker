package sh.karda.maptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, LocationTrackerService.class);
        context.startForegroundService(serviceIntent);
        Log.i("AutoStart", "Service started");
    }
}
