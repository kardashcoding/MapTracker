package sh.karda.maptracker.get;

import java.util.ArrayList;

import sh.karda.maptracker.dto.Point;

public class PositionPoints {
    ArrayList<Point> points;

    public PositionPoints() {
        points = new ArrayList<>();
    }

    public double getMinLatitude(){
        double minValue = 1000;
        for (Point item : points) {
            if (item.latitude < minValue) minValue = item.latitude;
        }
        return minValue;
    }
    public double getMinLongitude(){
        double minValue = 1000;
        for (Point item : points) {
            if (item.longitude < minValue) minValue = item.longitude;
        }
        return minValue;
    }

    public double getMaxLatitude(){
        double maxValue = -1000;
        for (Point item : points) {
            if (item.latitude > maxValue) maxValue = item.latitude;
        }
        return maxValue;
    }

    public double getMaxLongitude(){
        double maxValue = -1000;
        for (Point item : points) {
            if (item.longitude > maxValue) maxValue = item.longitude;
        }
        return maxValue;
    }
}
