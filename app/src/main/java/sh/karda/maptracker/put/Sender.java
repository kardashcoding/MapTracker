package sh.karda.maptracker.put;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import sh.karda.maptracker.MapsActivity;
import sh.karda.maptracker.PreferenceHelper;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.DbManager;

public class Sender extends AsyncTask<Void, Void, String> {
    private final static String urlStr = "https://azure-location-function-app.azurewebsites.net/api/LocationReceiver?code=wNwhR6L5QIVeWZqtY1mqCxWF/sl/Zqm/qt1FCjOfQh09Zm5I1P28vA==";
    private static final String TAG = "Sender";
    private String deviceId;
    private String action;

    public Sender(String action, String deviceId){
        this.action = action;
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
            if (action.equals("SEND")) {
                String result = PutRequest.send(urlStr);
                int i = markAsDeleted(result);
                return i + " was sent";
            }
            if (action.equals("DELETE")) {
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
            //if (PreferenceHelper.getToastFromPreferences()) {
            //    Toast.makeText(MapsActivity.getAppContext(), response, Toast.LENGTH_SHORT).show();
            //}
        }
    }

    private int markAsDeleted(String response) {
        try {
            int i = 0;
            Gson gson = new Gson();
            ArrayList<String> result = gson.fromJson(response, new TypeToken<ArrayList<String>>(){}.getType());
            if (result == null) return 0;
            for (String s: result) {
                i++;
                DbManager.SetRowsAsSent(s);
            }

            return i;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
