package sh.karda.maptracker.get;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import sh.karda.maptracker.R;
import sh.karda.maptracker.map.MapHelper;
import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class GetLocations extends AsyncTask<Void, Void, Positions> {
    private GoogleMap map;
    private String deviceId;
    private String TAG = "GetLocations";
    private final String url = "https://azure-location-function-app.azurewebsites.net/api/LocationReceiver?code=wNwhR6L5QIVeWZqtY1mqCxWF/sl/Zqm/qt1FCjOfQh09Zm5I1P28vA==";

    public GetLocations(GoogleMap map, String device){
        this.map = map;
        this.deviceId = device;
    }

    private JSONObject getJSONObjectFromURL() throws IOException, JSONException {
        HttpURLConnection urlConnection;
        String urlString = url + "&device=" + deviceId;
        Log.v(TAG, "URL: " + urlString);
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    @Override
    protected Positions doInBackground(Void... voids) {

        Positions points = new Positions();

        try {
            JSONObject json = getJSONObjectFromURL();
            Gson gson = new Gson();
            JSONArray arr = json.getJSONArray("points");

            for (int i = 0; i < arr.length(); i++) {
                String line = arr.getJSONObject(i).toString();
                Point p = gson.fromJson(line, Point.class);
                if (p == null){
                    Log.v(TAG, "FAEN!");
                    Log.v(TAG, line);

                }else{
                    points.points.add(p);
                }
            }
            Log.v(TAG, "antall" + points.points.size());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return points;
    }

    @Override
    protected void onPostExecute(Positions points) {
        super.onPostExecute(points);
        MapHelper.addToMap(map, points);
    }

}
