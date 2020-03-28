package sh.karda.maptracker.database;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PositionRow {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "guid")
    private String guid;

    @ColumnInfo(name = "device")
    private String device;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "accuracy")
    private float accuracy;

    @ColumnInfo(name = "height")
    private double height;

    @ColumnInfo(name = "speed")
    private float speed;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "wifi")
    private String wifi;

    @ColumnInfo(name = "connected")
    private boolean connectedToWifi;

    public boolean sent;

    public PositionRow(String guid, String device, double longitude, double latitude, float accuracy, double height, float speed, Date date, String wifi, boolean connectedToWifi) {
        this.guid = guid;
        this.device = device;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.height = height;
        this.speed = speed;
        this.date = date;
        this.wifi = wifi;
        this.connectedToWifi = connectedToWifi;
        this.sent = false;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getWifi() {
        return wifi;
    }
    public boolean isConnectedToWifi() {
        return connectedToWifi;
    }

    public void setConnectedToWifi(boolean connectedToWifi) {
        this.connectedToWifi = connectedToWifi;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeString(){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
        return localDateFormat.format(date);
    }

    public String getDateString(){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
        return localDateFormat.format(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
