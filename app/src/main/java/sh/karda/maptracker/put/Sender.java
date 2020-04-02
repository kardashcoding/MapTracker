package sh.karda.maptracker.put;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.dto.Positions;

public class Sender extends AsyncTask<Void, Void, String> {
    private final static String urlStr = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
    private static final String TAG = "Sender";
    private AppDatabase db;
    private Location location;

    public Sender(AppDatabase db){
        this.db = db;
    }

    public Sender(Location location){
        this.location = location;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void...params){
        try {
            Log.v(TAG, "Shit kom hit 1");
            if (db != null) return PutRequest.send(urlStr, db);
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
        }
    }
}
