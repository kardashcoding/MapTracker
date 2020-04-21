package sh.karda.maptracker.map;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class MapHelper {
    private static final String TAG = "MapHelper";

    public static void addToMap(GoogleMap map, Positions points){
        try {
            if (map == null || points == null || points.points == null || points.points.size() == 0) return;

            boolean drawLines = PreferenceHelper.getDownloadAutomatically();
            Point prev = null;
            long deltaTime = 0;

            int delta = calculateMarkerDistance(points.points.size());
            if (delta < 0) return;
            map.clear();
            for (Point item: points.points) {
                LatLng myLocation = new LatLng(item.latitude, item.longitude);
                Log.v(TAG, "Adding latitude: " + item.latitude + " longitude: " + item.longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(myLocation)
                        .title(item.getTime())
                        .snippet(item.getSnippet())
                        .icon(BitmapDescriptorFactory.defaultMarker(item.getSpeedColor()));
                if (delta == 0){
                    map.addMarker(markerOptions);
                }else{
                    if (prev != null){
                        long diff = item.date.getTime() - prev.date.getTime();
                        deltaTime = deltaTime + diff;
                        if (diff >= delta * 1000 || deltaTime >= delta *1000){
                            map.addMarker(markerOptions);
                            deltaTime = 0;
                        }
                    }
                }

                if (prev != null && drawLines){
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(prev.latitude, prev.longitude), new LatLng(item.latitude, item.longitude))
                            .width(10)
                            .color(item.getLineColor()));
                }
                prev = item;
            }

            LatLngBounds myArea = new LatLngBounds(new LatLng(points.getMinLatitude(), points.getMinLongitude()), new LatLng(points.getMaxLatitude(), points.getMaxLongitude()));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(myArea, 200);
            map.moveCamera(cameraUpdate);
            map.animateCamera(cameraUpdate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Feil i Marker/Zoom delen");
        }
    }

    private static int calculateMarkerDistance(int size) {
        if (size < 50) return 0;
        if (size < 200) return 60;
        if (size < 400) return 60*2;
        if (size < 600) return 60*3;
        if (size < 800) return 60*4;
        if (size < 1000) return 60*5;
        if (size < 10000) return 60*10;
        if (size < 100000) return 60*100;

        return -1;
    }

    public static void addSingleLocationToMap(GoogleMap map, Location location){
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLocation)
                .title(convertTime(location.getTime()))
                .snippet(getSnippet(location))
                .icon(BitmapDescriptorFactory.defaultMarker(getSpeedColor(location.getSpeed())));
        map.addMarker(markerOptions);
    }

    public static void moveCamera(ArrayList<Location> locations, GoogleMap map){
        if (locations.size() < 2) return;
        double minLat = getMinLatitude(getLast(locations));
        double minLng = getMinLongitude(getLast(locations));
        double maxLat = getMaxLatitude(getLast(locations));
        double maxLng = getMaxLongitude(getLast(locations));
        LatLngBounds myArea = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(myArea, 200);
        map.moveCamera(cameraUpdate);
        map.animateCamera(cameraUpdate);
    }

    private static List<Location> getLast(ArrayList<Location> locations) {
        List<Location> last;
        if (locations.size() <= 20){
            return locations;
        }
        last = locations.subList(locations.size() - 20, locations.size());
        return last;
    }

    private static double getMinLatitude(List<Location> points){
        double minValue = 1000;
        for (Location item : points) {
            if (item.getLatitude() < minValue) minValue = item.getLatitude();
        }
        return minValue;
    }
    private static double getMinLongitude(List<Location> points){
        double minValue = 1000;
        for (Location item : points) {
            if (item.getLongitude() < minValue) minValue = item.getLongitude();
        }
        return minValue;
    }

    private static double getMaxLatitude(List<Location> points){
        double MaxValue = -1000;
        for (Location item : points) {
            if (item.getLatitude() > MaxValue) MaxValue = item.getLatitude();
        }
        return MaxValue;
    }
    private static double getMaxLongitude(List<Location> points){
        double MaxValue = -1000;
        for (Location item : points) {
            if (item.getLongitude() > MaxValue) MaxValue = item.getLongitude();
        }
        return MaxValue;
    }

    private static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
        return format.format(date);
    }

    private static String getSnippet(Location location){
        return "Accuracy: " + location.getAccuracy()  + System.lineSeparator() +
                "Speed: " + (location.getSpeed() * 3.6) + System.lineSeparator() +
                "Height: " + location.getAltitude();
    }

    private static float getSpeedColor(float speed) {
        float multipliedSpeed = speed * 50;
        if (multipliedSpeed < 0) return 0;
        if (multipliedSpeed > 359) return 359;
        return multipliedSpeed;
    }

}
