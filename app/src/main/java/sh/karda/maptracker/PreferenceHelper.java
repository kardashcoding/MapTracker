package sh.karda.maptracker;

import android.content.SharedPreferences;
import android.location.Criteria;

import androidx.preference.PreferenceManager;

class PreferenceHelper {

    static int getAccuracyFromPreferences() {
        SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
        String accuracy = p.getString("key_accuracy", "no value found");
        if (accuracy.equals("ACCURACY_COARSE")) return Criteria.ACCURACY_COARSE;
        if (accuracy.equals("ACCURACY_FINE")) return Criteria.ACCURACY_FINE;
        if (accuracy.equals("ACCURACY_HIGH")) return Criteria.ACCURACY_HIGH;
        if (accuracy.equals("ACCURACY_LOW")) return Criteria.ACCURACY_LOW;
        if (accuracy.equals("ACCURACY_MEDIUM")) return Criteria.ACCURACY_MEDIUM;

        return Criteria.ACCURACY_MEDIUM;
    }
    static int getPowerFromPreferences() {
        SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
        String accuracy = p.getString("key_power", "no value found");
        if (accuracy.equals("POWER_HIGH")) return Criteria.POWER_HIGH;
        if (accuracy.equals("POWER_LOW")) return Criteria.POWER_LOW;
        if (accuracy.equals("POWER_MEDIUM")) return Criteria.POWER_MEDIUM;

        return Criteria.POWER_MEDIUM;
    }

    static int getSecondsFromPreferences(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            String value = p.getString("key_seconds", "5");
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 5;
        }
    }

    static int getDistanceFromPreferences(){
        try {
            SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
            String value = p.getString("key_distance", "5");
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 10;
        }
    }

}

