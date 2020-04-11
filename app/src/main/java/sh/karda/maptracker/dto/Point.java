package sh.karda.maptracker.dto;

import android.graphics.Color;

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

    private float accuracy;
    private boolean connected_to_wifi;
    public Date date;
    private String deleted;
    private String device;
    private String guid;
    public double height;
    public int id;
    public double latitude;
    public double longitude;
    private float speed;

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
}
