package sh.karda.maptracker.map;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.PositionRow;
import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class MapHelper {
    private static Point prev = null;

    private static String TAG = "MapHelper";
    private static ArrayList<String> addedToMap = new ArrayList<String>();

    public static void addToMap(GoogleMap map, AppDatabase db){
        List<PositionRow> rows = db.posDao().getLastDay(getDay(1), getDay(0));
        Positions points = new Positions();
        for (PositionRow row: rows) {
            points.points.add(new Point(row.getAccuracy(), row.isConnectedToWifi(), row.getDate(), row.getDevice(), row.getGuid(), row.getHeight(), row.getId(), row.getLatitude(), row.getLongitude(), row.getSpeed(), row.getWifi(), row.getDeleted()));
        }
        addToMap(map, points);
    }



    public static void addToMap(GoogleMap map, Positions points){
        try {
            if (map == null) return;
            if (points == null || points.points == null || points.points.size() < 3) return;
            map.clear();
            addedToMap.clear();
            boolean drawLines = PreferenceHelper.getDownloadAutomatically();

            prev = null;
            long deltaTime = 0;
            for (Point item: points.points) {
                if (addedToMap.contains(item.getGuid())) continue;
                addedToMap.add(item.getGuid());
                LatLng myLocation = new LatLng(item.latitude, item.longitude);
                Log.v(TAG, "Adding latitude: " + item.latitude + " longitude: " + item.longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(myLocation)
                        .title(item.getTime())
                        .snippet(item.getSnippet())
                        .icon(BitmapDescriptorFactory.defaultMarker(item.getSpeedColor()));
                if (prev != null){
                    long diff = item.date.getTime() - prev.date.getTime();
                    deltaTime = deltaTime + diff;
                    if (diff >= 60000 || deltaTime >= 60000){
                        map.addMarker(markerOptions);
                        deltaTime = 0;
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

    private static long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTimeInMillis();
    }
}
