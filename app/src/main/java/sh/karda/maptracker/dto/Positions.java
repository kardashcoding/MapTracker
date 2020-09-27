package sh.karda.maptracker.dto;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import sh.karda.maptracker.database.AppDatabase;

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

    public void analyzeActivities(){
        String currentActivity = "";
        int start = 0;
        for(int i=0; i< points.size(); i++){

            if (points.get(i).avgSpeed * 3.6 < 5){
                points.get(i).activity = "Walk";
                if (!currentActivity.equals(points.get(i).activity)){
                    currentActivity = points.get(i).activity;
                    if (i > 0) {
                        int midPoint = start + ((i-1) - start)/2;
                        points.get(midPoint).midPoint = true;
                    }
                    start = i;
                }
            }
            if (points.get(i).avgSpeed * 3.6 >= 5 && points.get(i).avgSpeed * 3.6 < 12){
                points.get(i).activity = "Run";
                if (!currentActivity.equals(points.get(i).activity)){
                    currentActivity = points.get(i).activity;
                    if (i > 0) {
                        int midPoint = start + ((i-1) - start)/2;
                        points.get(midPoint).midPoint = true;
                    }
                    start = i;
                }
            }
            if (points.get(i).avgSpeed * 3.6 >= 12 && points.get(i).avgSpeed * 3.6 < 40){
                points.get(i).activity = "Cycle";
                if (!currentActivity.equals(points.get(i).activity)){
                    currentActivity = points.get(i).activity;
                    if (i > 0) {
                        int midPoint = start + ((i-1) - start)/2;
                        points.get(midPoint).midPoint = true;
                    }
                    start = i;
                }
            }
            if (points.get(i).avgSpeed * 3.6 >= 40 && points.get(i).avgSpeed * 3.6 < 120){
                points.get(i).activity = "Car";
                if (!currentActivity.equals(points.get(i).activity)){
                    currentActivity = points.get(i).activity;
                    if (i > 0) {
                        int midPoint = start + ((i-1) - start)/2;
                        points.get(midPoint).midPoint = true;
                    }
                    start = i;
                }
            }
            if (points.get(i).avgSpeed * 3.6 >= 120){
                points.get(i).activity = "Jet Plane";
                if (!currentActivity.equals(points.get(i).activity)){
                    if (i > 0) {
                        int midPoint = start + ((i-1) - start)/2;
                        points.get(midPoint).midPoint = true;
                    }
                    start = i;
                }
            }
        }
    }
    public void calculateAvgSpeed(){
        for(int i=0; i< points.size(); i++){
            float sum = 0;
            int counter=0;
            if (i-5>=0) {
                sum = sum + points.get(i-5).speed;
                counter++;
            }
            if (i-4>=0) {
                sum = sum + points.get(i-4).speed;
                counter++;
            }
            if (i-3>=0) {
                sum = sum + points.get(i-3).speed;
                counter++;
            }
            if (i-2>=0) {
                sum = sum + points.get(i-2).speed;
                counter++;
            }
            if (i-1>=0) {
                sum = sum + points.get(i-1).speed;
                counter++;
            }
            sum = sum + points.get(i).speed;
            counter++;
            if (i+1<points.size()) {
                sum = sum + points.get(i+1).speed;
                counter++;
            }
            if (i+2<points.size()) {
                sum = sum + points.get(i+2).speed;
                counter++;
            }
            if (i+3<points.size()) {
                sum = sum + points.get(i+3).speed;
                counter++;
            }
            if (i+4<points.size()) {
                sum = sum + points.get(i+4).speed;
                counter++;
            }
            if (i+5<points.size()) {
                sum = sum + points.get(i+5).speed;
                counter++;
            }
            points.get(i).avgSpeed = sum/counter;
        }
    }

    public void printCsv(){
        Log.v("Csv","Id;Date;Activity;Speed;AvgSpeed;Accuracy");
        for(int i=0; i< points.size(); i++){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
            String strDate = dateFormat.format(points.get(i).date);
            Log.v("Csv", i +";" + strDate  +";" + points.get(i).activity  +";" + points.get(i).speed+";" + points.get(i).avgSpeed+";" + points.get(i).accuracy);
        }
    }

}
