package sh.karda.maptracker.map;

import android.graphics.Color;
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
import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.database.PositionRow;
import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class MapHelper {
    private static Point prev = null;
    private static String TAG = "MapHelper";
    private static ArrayList<String> addedToMap = new ArrayList<String>();

    public static void addCurrentPositionToMap(GoogleMap map, PositionRow item){
        boolean drawLines = PreferenceHelper.getDownloadAutomatically();

        LatLng myLocation = new LatLng(item.getLatitude(), item.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(markerOptions);
        if (prev != null && drawLines){
            map.addPolyline(new PolylineOptions()
                    .add(new LatLng(prev.latitude, prev.longitude), new LatLng(item.getLatitude(), item.getLongitude()))
                    .width(15));
            prev.latitude = item.getLatitude();
            prev.longitude = item.getLongitude();
        }
    }

    public static void addToMap(GoogleMap map, Positions points){
        try {
            if (map == null) return;
            if (points == null || points.points == null || points.points.size() == 0) return;
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
}
