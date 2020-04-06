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

    private float accuracy;
    private boolean connected_to_wifi;
    public String date;
    private String device;
    private String guid;
    public double height;
    public int id;
    public double latitude;
    public double longitude;
    private float speed;
    private String wifi;

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
        if (date == null) return "";
        String[] split = date.split("T");
        if (split.length != 2) return date;
        String time = split[1];
        return time.split("\\.")[0];
    }

    public String getSnippet(){
        return "Time: " + getTime()  + System.lineSeparator() +
                "Accuracy: " + getAccuracy()  + System.lineSeparator() +
                "Speed: " + getSpeed() + System.lineSeparator() +
                "Height: " + getHeight()  + System.lineSeparator() + wifi;

    }

    public float getSpeedColor() {
        float multipliedSpeed = speed * 50;
        if (multipliedSpeed < 0) return 0;
        if (multipliedSpeed > 359) return 359;
        return multipliedSpeed;
    }
}
