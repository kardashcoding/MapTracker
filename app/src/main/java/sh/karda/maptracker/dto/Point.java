package sh.karda.maptracker.dto;


import java.util.Date;
import java.time.Instant;

public class Point {
    public float accuracy;
    public boolean connected_to_wifi;
    public Date date;
    public String device;
    public String guid;
    public double height;
    public int id;
    public double latitude;
    public double longitude;
    public float speed;
    public String wifi;

    public String getTitle() {
            return "not set";
    }
}
