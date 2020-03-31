package sh.karda.maptracker.dto;

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

    public int getSpeed(){
        return (int) speed;
    }

    public int getAccuracy(){
        return (int) accuracy;
    }

    public int getHeight(){
        return (int) height;
    }

    public String getTime(){
        if (date == null) return "";
        String[] split = date.split("T");
        if (split.length != 2) return date;
        String time = split[1];
        String r = time.split("\\.")[0];
        return r;
    }

    public String getTitle(){
        return getTime() + System.lineSeparator() +
                "Speed: " + getSpeed()  + System.lineSeparator() +
                "Accuracy: " + getAccuracy()  + System.lineSeparator() +
                "Height: " + getHeight()  + System.lineSeparator() + wifi;

    }
}
