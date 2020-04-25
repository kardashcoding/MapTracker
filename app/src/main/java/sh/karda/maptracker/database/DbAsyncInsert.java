package sh.karda.maptracker.database;

import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import sh.karda.maptracker.MapsActivity;
import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.R;
import sh.karda.maptracker.dto.Positions;
import sh.karda.maptracker.map.MapHelper;


public class DbAsyncInsert extends AsyncTask<Void, Void, PositionRow> {

    private AppDatabase db;
    private Location location;
    private String deviceId;
    private boolean gotNetwork;
    private String wifiName;

    public DbAsyncInsert(AppDatabase db, Location location, String deviceId, boolean networkAvailable, String wifiName) {
        this.db = db;
        this.location = location;
        this.deviceId = deviceId;
        this.gotNetwork = networkAvailable;
        this.wifiName = wifiName;
    }

    @Override
    protected PositionRow doInBackground(Void... voids) {
        PositionRow row = locationToPositionRow(location, deviceId);
        db.posDao().insertRow(row);
        return null;
    }

    private PositionRow locationToPositionRow(Location location, String deviceId){
        long millis=System.currentTimeMillis();
        return new PositionRow(java.util.UUID.randomUUID().toString(),
                deviceId, location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                location.getAltitude(), location.getSpeed(), new java.sql.Date(millis), wifiName, null, gotNetwork);
    }

    @Override
    protected void onPostExecute(PositionRow points) {
        super.onPostExecute(points);
        if (MapsActivity.getAppContext() != null && PreferenceHelper.getPlaySoundFromPreferences()){
            MediaPlayer player = MediaPlayer.create(MapsActivity.getAppContext(), R.raw.drip);
            player.start();
        }
    }
}
