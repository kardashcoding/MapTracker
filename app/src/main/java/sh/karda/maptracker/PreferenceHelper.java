package sh.karda.maptracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;

import com.google.android.gms.location.LocationRequest;

import androidx.preference.PreferenceManager;

public class PreferenceHelper {

    public static int getAccuracyFromPreferences() {
        SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
        String accuracy = p.getString("key_accuracy", "no value found");
        if (accuracy.equals("PRIORITY_BALANCED_POWER_ACCURACY")) return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        if (accuracy.equals("PRIORITY_HIGH_ACCURACY")) return LocationRequest.PRIORITY_HIGH_ACCURACY;
        if (accuracy.equals("PRIORITY_LOW_POWER")) return LocationRequest.PRIORITY_LOW_POWER;
        if (accuracy.equals("PRIORITY_NO_POWER")) return LocationRequest.PRIORITY_NO_POWER;

        return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    }
    public static int getPowerFromPreferences() {
        SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
        String accuracy = p.getString("key_power", "no value found");
        if (accuracy.equals("POWER_HIGH")) return Criteria.POWER_HIGH;
        if (accuracy.equals("POWER_LOW")) return Criteria.POWER_LOW;
        if (accuracy.equals("POWER_MEDIUM")) return Criteria.POWER_MEDIUM;

        return Criteria.POWER_MEDIUM;
    }

    public static int getSecondsFromPreferences(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            String value = p.getString("key_seconds", "5");
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 5;
        }
    }

    public static int getDistanceFromPreferences(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            String value = p.getString("key_distance", "5");
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 10;
        }
    }

    public static boolean getToastFromPreferences(){
        try{
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            return p.getBoolean("key_toast", false);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    static boolean getDrawLinesFromPreferences(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            return p.getBoolean("key_lines", false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean drawEveryMarker(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            return p.getBoolean("key_marker", false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean getSyncOnlyOnWifi() {
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            return p.getBoolean("key_sync", false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean getDownloadAutomatically() {
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            return p.getBoolean("key_download", false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}

