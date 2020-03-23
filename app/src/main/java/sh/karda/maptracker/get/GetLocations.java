package sh.karda.maptracker.get;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetLocations extends AsyncTask<Void, Void, PositionPoints> {
    private GoogleMap map;
    private String deviceId;
    private String TAG = "GetLocations";
    private PositionPoints dataItems;
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
    protected PositionPoints doInBackground(Void... voids) {
        try {
            JSONObject json = getJSONObjectFromURL();
            Gson gson = new Gson();

            String s = json.toString();
            dataItems = gson.fromJson(s, PositionPoints.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return dataItems;
    }

    @Override
    protected void onPostExecute(PositionPoints points){
        super.onPostExecute(points);

        for (Point item: points.items) {
            LatLng myLocation = new LatLng(item.latitude, item.longitude);
            Log.v(TAG, "Adding latitude: " + item.latitude + " longitude: " + item.longitude);
            map.addMarker(new MarkerOptions().position(myLocation).title(item.date));
        }
        LatLngBounds myArea = new LatLngBounds(new LatLng(points.getMinLatitude(), points.getMinLongitude()), new LatLng(points.getMaxLatitude(), points.getMaxLongitude()));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(myArea, 200));
    }

}
