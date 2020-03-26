package sh.karda.maptracker.dto;

import android.location.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;

public class Positions {
    public ArrayList<Point> points = new ArrayList<>();

    public void addPoint(Location location, String deviceId) {
        Point point = new Point();
        point.accuracy = location.getAccuracy();
        point.date = Calendar.getInstance().getTime().toString();
        point.height = location.getAltitude();
        point.latitude = location.getLatitude();
        point.longitude = location.getLongitude();
        point.speed = location.getSpeed();
        points.add(point);
    }
}
