package sh.karda.maptracker.put;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

class DataPackager {
    private String device;
    private String latitude;
    private String longitude;
    private String height;
    private String speed;
    private String TAG = "DataPacker";

    DataPackager(String device, String latitude, String longitude, String height, String speed){
        this.device = device;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.speed = speed;
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    String packData(){
        JSONObject jsonObject = new JSONObject();
        StringBuilder packedData = new StringBuilder();

        try {
            jsonObject.put("Device", device);
            jsonObject.put("Latitude", latitude);
            jsonObject.put("Longitude", longitude);
            jsonObject.put("Height", height);
            jsonObject.put("Speed", speed);

            Boolean firstValue = true;
            Iterator iterator = jsonObject.keys();

            do {
                String key = iterator.next().toString();
                String value = jsonObject.get(key).toString();
                if (firstValue){
                    firstValue = false;
                }else{
                    packedData.append("&");
                }

                packedData.append(URLEncoder.encode(key, "UTF-8"));
                packedData.append(("="));
                packedData.append(URLEncoder.encode(value, "UTF-8"));

            } while (iterator.hasNext());

            String returnString = packedData.toString();
            Log.v(TAG, "Jason is:" + returnString);
            return returnString;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
