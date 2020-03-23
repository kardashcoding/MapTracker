package sh.karda.maptracker.get;

import java.util.ArrayList;

class PositionPoints {
    ArrayList<Point> items;

    public float getMinLatitude(){
        float minValue = 1000;
        for (Point item : items) {
            if (item.latitude < minValue) minValue = item.latitude;
        }
        return minValue;
    }
    public float getMinLongitude(){
        float minValue = 1000;
        for (Point item : items) {
            if (item.longitude < minValue) minValue = item.longitude;
        }
        return minValue;
    }

    public float getMaxLatitude(){
        float maxValue = -1000;
        for (Point item : items) {
            if (item.latitude > maxValue) maxValue = item.latitude;
        }
        return maxValue;
    }

    public float getMaxLongitude(){
        float maxValue = -1000;
        for (Point item : items) {
            if (item.longitude > maxValue) maxValue = item.longitude;
        }
        return maxValue;
    }
}
