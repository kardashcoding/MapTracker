package sh.karda.maptracker.database;

import android.location.Location;
import android.os.AsyncTask;


public class DbAsyncInsert extends AsyncTask<Void, Void, String> {

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
    protected String doInBackground(Void... voids) {
        db.posDao().insertRow(locationToPositionRow(location, deviceId));
        return null;
    }

    private PositionRow locationToPositionRow(Location location, String deviceId){
        long millis=System.currentTimeMillis();
        return new PositionRow(java.util.UUID.randomUUID().toString(),
                deviceId, location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                location.getAltitude(), location.getSpeed(), new java.sql.Date(millis), wifiName, null, gotNetwork);
    }
}
