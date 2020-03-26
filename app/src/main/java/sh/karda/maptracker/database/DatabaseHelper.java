package sh.karda.maptracker.database;

import android.location.Location;
import android.os.AsyncTask;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class DatabaseHelper extends AsyncTask<Void, Void, String> {

    private AppDatabase db;
    private Location location;
    private String deviceId;
    private Location mLastLocation;


    public DatabaseHelper(AppDatabase db, Location location, String deviceId) {
        this.db = db;
        this.location = location;
        this.deviceId = deviceId;
    }

    @Override
    protected String doInBackground(Void... voids) {
        db.posDao().insertRow(locationToPositionRow(location, deviceId));
        return null;
    }

    static PositionRow locationToPositionRow(Location location, String deviceId){
        return new PositionRow(java.util.UUID.randomUUID().toString(),
                deviceId, location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                location.getAltitude(), location.getSpeed(), Iso8603DateFormat());
    }

    static String Iso8603DateFormat(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(Calendar.getInstance().getTime());

    }
}
