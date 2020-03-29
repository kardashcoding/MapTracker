package sh.karda.maptracker.dto;


import java.util.Date;
import java.time.Instant;

public class Point {
    public Point(float accuracy, boolean connected_to_wifi, String date, String device, String guid, double height, int id, double latitude, double longitude, float speed, String wifi) {
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
        this.wifi = wifi;
    }

    public float accuracy;
    public boolean connected_to_wifi;
    public String date;
    public String device;
    public String guid;
    public double height;
    public int id;
    public double latitude;
    public double longitude;
    public float speed;
    public String wifi;

}
