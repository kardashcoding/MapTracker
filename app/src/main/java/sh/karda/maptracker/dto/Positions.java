package sh.karda.maptracker.dto;

import java.util.ArrayList;

public class Positions {
    public ArrayList<Point> points;

    public Positions() {
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
