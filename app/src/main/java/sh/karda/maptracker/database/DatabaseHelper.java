package sh.karda.maptracker.database;

import android.location.Location;
import android.os.AsyncTask;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import sh.karda.maptracker.put.Sender;


public class DatabaseHelper extends AsyncTask<Void, Void, String> {

    private AppDatabase db;
    private Location location;
    private String deviceId;
    private Location mLastLocation;
    private boolean gotNetwork;
    private String wifiName;


    public DatabaseHelper(AppDatabase db, Location location, String deviceId, boolean networkAvailable, String wifiName) {
        this.db = db;
        this.location = location;
        this.deviceId = deviceId;
        this.gotNetwork = networkAvailable;
        this.wifiName = wifiName;
    }

    @Override
    protected String doInBackground(Void... voids) {
        db.posDao().insertRow(locationToPositionRow(location, deviceId));
        Sender sender = new Sender(db);
        sender.execute();
        return null;
    }

    private PositionRow locationToPositionRow(Location location, String deviceId){
        long millis=System.currentTimeMillis();
        return new PositionRow(java.util.UUID.randomUUID().toString(),
                deviceId, location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                location.getAltitude(), location.getSpeed(), new java.sql.Date(millis), wifiName, gotNetwork);
    }
}
