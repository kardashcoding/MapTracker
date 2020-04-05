package sh.karda.maptracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;

import androidx.preference.PreferenceManager;

class PreferenceHelper {

    private static SharedPreferences prefs;
    static int getAccuracyFromPreferences() {
        SharedPreferences p =  PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());
        String accuracy = p.getString("accuracy", "no value found");
        if (accuracy.equals("ACCURACY_COARSE")) return Criteria.ACCURACY_COARSE;
        if (accuracy.equals("ACCURACY_FINE")) return Criteria.ACCURACY_FINE;
        if (accuracy.equals("ACCURACY_HIGH")) return Criteria.ACCURACY_HIGH;
        if (accuracy.equals("ACCURACY_LOW")) return Criteria.ACCURACY_LOW;
        if (accuracy.equals("ACCURACY_MEDIUM")) return Criteria.ACCURACY_MEDIUM;

        return Criteria.ACCURACY_MEDIUM;
    }


}

