package sh.karda.maptracker.get;

import java.util.regex.Pattern;

public class Point {
    public int id;
    public String device;
    float longitude;
    float latitude;
    float accuracy;
    public float height;
    public float speed;
    public String date;

    public String getTitle() {
            if (date == null || date == "" || !date.contains("T")) return date;
            String time = date.split("T")[1];
            if (time == null || time == "" || !time.contains(".")) return date;
            String returnTime = time.split(Pattern.quote("."))[0];
            if (returnTime == null || returnTime == "") return date;
            return returnTime;
    }
}
