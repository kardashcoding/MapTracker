package sh.karda.maptracker.dto;

import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Point {
    public Point(float accuracy, boolean connected_to_wifi, Date date, String device, String guid, double height, int id, double latitude, double longitude, float speed, String wifi, String deleted) {
        this.accuracy = accuracy;
        this.connected_to_wifi = connected_to_wifi;
        this.date = date;
        this.device = device;
        this.guid = guid;
        this.height = height;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.deleted = deleted;
    }

    float accuracy;
    private boolean connected_to_wifi;
    public Date date;
    private String deleted;
    private String device;
    private double distance;
    private String guid;
    public double height;
    public int id;
    public double latitude;
    public double longitude;
    float speed;
    float avgSpeed;
    public String activity;
    public boolean midPoint = false;

    public String getGuid(){
        return guid;
    }

    private int getAccuracy(){
        return (int) accuracy;
    }

    public int getHeight(){
        return (int) height;
    }

    private int getSpeed(){
        return (int) (3.6 * speed);
    }

    public String getTime(){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        return localDateFormat.format(date);
    }

    public String getSnippet(){
        return "Accuracy: " + getAccuracy()  + System.lineSeparator() +
                "Speed: " + getSpeed() + System.lineSeparator() +
                "Height: " + getHeight();
    }

    public float getSpeedColor() {
        float multipliedSpeed = speed * 50;
        if (multipliedSpeed < 0) return 0;
        if (multipliedSpeed > 359) return 359;
        return multipliedSpeed;
    }

    public int getLineColor() {
        if ((int)speed < 1) return Color.rgb(134, 1, 175);
        if ((int)speed == 2) return Color.rgb(61, 1, 164);
        if ((int)speed == 3) return Color.rgb(2, 71, 254);
        if ((int)speed == 4) return Color.rgb(3, 146, 206);
        if ((int)speed == 5) return Color.rgb(102, 176, 102);
        if ((int)speed == 6) return Color.rgb(208, 234, 43);
        if ((int)speed == 7) return Color.rgb(254, 254, 51);
        if ((int)speed == 8) return Color.rgb(250, 188, 2);
        if ((int)speed == 9) return Color.rgb(251, 153, 2);
        if ((int)speed == 10) return Color.rgb(253, 83, 8);
        if ((int)speed > 10) return Color.rgb(167, 25, 75);
        return Color.BLUE;
    }

    public int getLineColor(String activity) {
        float tempSpeed = 0;
        if (activity.equals("Walk")){
            tempSpeed = avgSpeed*3.6f * 10/5;
        }else if (activity.equals("Run")){
            tempSpeed = ((avgSpeed * 3.6f) - 5);
            tempSpeed = tempSpeed * 10/7;
        }else if (activity.equals("Cycle")){
            tempSpeed = ((avgSpeed * 3.6f) - 12);
            tempSpeed = tempSpeed * 10/28;
        }else if (activity.equals("Car")){
            tempSpeed = ((avgSpeed * 3.6f) - 40);
            tempSpeed = tempSpeed * 10/80;
        }else if (activity.equals("Jet Plane")){
            tempSpeed = ((avgSpeed * 3.6f) - 120);
            tempSpeed = tempSpeed * 10/50;
        }
        Log.v("LineColor", "Activity: " + activity + " Avg Speed: " + avgSpeed*3.6 + " Weighted speed: " + tempSpeed);
        if ((int)tempSpeed < 1) return Color.rgb(134, 1, 175);
        if ((int)tempSpeed == 2) return Color.rgb(61, 1, 164);
        if ((int)tempSpeed == 3) return Color.rgb(2, 71, 254);
        if ((int)tempSpeed == 4) return Color.rgb(3, 146, 206);
        if ((int)tempSpeed == 5) return Color.rgb(102, 176, 102);
        if ((int)tempSpeed == 6) return Color.rgb(208, 234, 43);
        if ((int)tempSpeed == 7) return Color.rgb(254, 254, 51);
        if ((int)tempSpeed == 8) return Color.rgb(250, 188, 2);
        if ((int)tempSpeed == 9) return Color.rgb(251, 153, 2);
        if ((int)tempSpeed == 10) return Color.rgb(253, 83, 8);
        if ((int)tempSpeed > 10) return Color.rgb(167, 25, 75);
        return Color.BLUE;
    }
}
