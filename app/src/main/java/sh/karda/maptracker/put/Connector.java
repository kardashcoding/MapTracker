package sh.karda.maptracker.put;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class Connector {
    private static String TAG = "Connector";
    static HttpURLConnection connect(String urlAddress){
        try
        {
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(2000);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            return conn;
        }catch (MalformedURLException e){
            Log.e(TAG, "Noe galt med URL");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
