package sh.karda.maptracker.put;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import sh.karda.maptracker.MapsActivity;
import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.database.AppDatabase;

public class Sender extends AsyncTask<Void, Void, String> {
    private final static String urlStr = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
    private static final String TAG = "Sender";
    private AppDatabase db;
    private String deviceId;

    public Sender(AppDatabase db){
        this.db = db;
    }

    public Sender(String deviceId){
        this.deviceId = deviceId;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void...params){
        try {
            Log.v(TAG, "Shit kom hit 1");
            if (db != null) {
                String result = PutRequest.send(urlStr, db);
                int i = markAsDeleted(result);
                return i + " was sent";
            }
            if (deviceId != null) {
                return PutRequest.delete(urlStr, deviceId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response){
        super.onPostExecute(response);

        if (response != null){
            Log.v(TAG, response);
            if (PreferenceHelper.getToastFromPreferences()) {
                Toast.makeText(MapsActivity.getAppContext(), response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int markAsDeleted(String response) {
        int i = 0;
        Gson gson = new Gson();
        ArrayList<String> result = gson.fromJson(response, new TypeToken<ArrayList<String>>(){}.getType());
        for (String s: result) {
            i++;
            db.posDao().setRowsAsSent(s);
        }

        return i;
    }
}
