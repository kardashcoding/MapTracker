package sh.karda.maptracker;

import java.util.Calendar;

public class Variables {
    public static boolean isNetworkConnected = false;
    private static int lastUsedMinDistance;

    static int getLastUsedMinDistance() {
        if (lastUsedMinDistance == 0 ||lastUsedMinDistance == 1000) return 7;
        return lastUsedMinDistance;
    }

    static void setLastUsedMinDistance(int lastUsedMinDistance) {
        Variables.lastUsedMinDistance = lastUsedMinDistance;
    }

    static int getLastUsedMinSeconds() {
        if (lastUsedMinSeconds == 0 || lastUsedMinSeconds == 1000) return 6;
        return lastUsedMinSeconds;
    }

    static void setLastUsedMinSeconds(int lastUsedMinSeconds) {
        Variables.lastUsedMinSeconds = lastUsedMinSeconds;
    }

    private static int lastUsedMinSeconds;
    public static String wiFiName;

    public static long getNow(){
        return getDay(0);
    }

    private static long timeValue;
    static void setFromTime(String time){
        if (time.equals(numberPickerValues[0])) timeValue = getDay(1);
        else if (time.equals(numberPickerValues[1])) timeValue = getDay(2);
        else if (time.equals(numberPickerValues[2])) timeValue = getDay(8);
        else if (time.equals(numberPickerValues[3])) timeValue = getDay(24);
        else if (time.equals(numberPickerValues[4])) timeValue = getDay(48);
        else timeValue = getDay(4);
    }

    private static long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -i);
        return cal.getTimeInMillis();
    }

    public static long getFromTime(){
        if (timeValue < 100000) {
            return getDay(24);
        }
        return timeValue;
    }

    static String[] numberPickerValues = new String[] { "1 Hour", "2 Hours", "8 Hours", "24 Hours", "48 Hours"};

}
