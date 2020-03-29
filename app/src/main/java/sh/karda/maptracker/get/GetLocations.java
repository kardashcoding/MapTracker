package sh.karda.maptracker.get;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class GetLocations extends AsyncTask<Void, Void, Positions> {
    private GoogleMap map;
    private String deviceId;
    private String TAG = "GetLocations";
    private Positions dataItems;
    public GetLocations(GoogleMap map, String device){
        this.map = map;
        this.deviceId = device;
    }

    private JSONObject getJSONObjectFromURL() throws IOException, JSONException {
        HttpURLConnection urlConnection;
        String urlString = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
        urlString = urlString + "&device=" + deviceId;
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
            //Gson gson = new GsonBuilder()
            //        .registerTypeAdapter(Date.class, new DateDeserializer()).create();
            //GsonBuilder gsonBuilder = gson.newBuilder();
            //gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());

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
            dataItems = points;
            Log.v(TAG, "antall" + points.points.size());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return points;
    }

    @Override
    protected void onPostExecute(Positions points){
        super.onPostExecute(points);

        if (points == null || points.points == null || points.points.size() < 3) return;
        map.clear();
        PolylineOptions options = new PolylineOptions();
        options.color(Color.BLUE)
                .width(5)
                .visible(true);
        Point prev = null;
        for (Point item: points.points) {
            LatLng myLocation = new LatLng(item.latitude, item.longitude);
            Log.v(TAG, "Adding latitude: " + item.latitude + " longitude: " + item.longitude);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(myLocation)
                    .title(item.date);
            map.addMarker(markerOptions);

            if (prev != null) {
                // options.add(new LatLng(prev.latitude, prev.longitude), new LatLng(item.latitude, item.longitude));
            }
            prev = item;
            map.addPolyline(options);
        }
        LatLngBounds myArea = new LatLngBounds(new LatLng(points.getMinLatitude(), points.getMinLongitude()), new LatLng(points.getMaxLatitude(), points.getMaxLongitude()));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(myArea, 200));
    }

}
