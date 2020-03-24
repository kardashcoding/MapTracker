package sh.karda.maptracker;

import android.location.Location;

public class LocationHelper {
    static Location mLastLocation;
    public static String getSpeed(Location pCurrentLocation) {
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
        return String.valueOf(speed);
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
}
