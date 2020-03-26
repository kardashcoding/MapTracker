package sh.karda.maptracker;

import android.location.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.PositionRow;
import sh.karda.maptracker.dto.Positions;
import sh.karda.maptracker.put.Sender;

class LocationHelper {
    private static Location mLastLocation;
    private static Positions positions = new Positions();
    static float getSpeed(Location pCurrentLocation) {
        double speed = 0;
        if (mLastLocation != null){
            double lat1 = mLastLocation.getLatitude();
            double lon1 = mLastLocation.getLatitude();
            double lat2 = pCurrentLocation.getLatitude();
            double lon2 = pCurrentLocation.getLatitude();
            double length = latLongToMeters(lat1, lon1, lat2, lon2);
            double time = (pCurrentLocation.getTime() - mLastLocation.getTime()) * 1000;
            if (time > 0)
                speed = length / time;
        }

        if (pCurrentLocation.hasSpeed())
            speed = pCurrentLocation.getSpeed();
        mLastLocation = pCurrentLocation;
        return (float) speed;
    }

    private static double latLongToMeters(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    static void storeLocation(Location location, String deviceId, AppDatabase db) {
        db.posDao().insertRow(locationToPositionRow(location, deviceId));
    }

    static PositionRow locationToPositionRow(Location location, String deviceId){
        return new PositionRow(java.util.UUID.randomUUID().toString(),
                deviceId, location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                location.getAltitude(), getSpeed(location), Iso8603DateFormat());
    }

    static String Iso8603DateFormat(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(Calendar.getInstance().getTime());
    }
}
