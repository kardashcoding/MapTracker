package sh.karda.maptracker.dto;


import java.sql.Date;
import java.time.Instant;

public class Point {
    public int id;
    public String device;
    public double longitude;
    public double latitude;
    float accuracy;
    public double height;
    public float speed;
    public String date;

    public String getTitle() {
            return "not set";
    }
}
