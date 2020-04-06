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

import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class MapHelper {
    private static Point prev = null;

    private static String TAG = "MapHelper";

    public static void addToMap(GoogleMap map, Positions points, boolean drawLines){
        try {
            if (map == null) return;
            if (points == null || points.points == null || points.points.size() < 3) return;
            map.clear();

            for (Point item: points.points) {
                LatLng myLocation = new LatLng(item.latitude, item.longitude);
                Log.v(TAG, "Adding latitude: " + item.latitude + " longitude: " + item.longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(myLocation)
                        .title(item.getTime())
                        .snippet(item.getSnippet())
                        .icon(BitmapDescriptorFactory.defaultMarker(item.getSpeedColor()));

                map.addMarker(markerOptions);

                if (prev != null && drawLines){
                    map.addPolyline(new PolylineOptions()
                            .add(new LatLng(prev.latitude, prev.longitude), new LatLng(item.latitude, item.longitude))
                            .width(5)
                            .color(Color.BLUE));
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
