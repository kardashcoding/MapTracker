package sh.karda.maptracker.put;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class Sender extends AsyncTask<Void, Void, String> {
    private static final String TAG = "Sender";
    private String urlAddress;
    private String device, latitude, longitude, height, speed, accuracy;

    public Sender(String urlAddress, String... values){
        this.urlAddress = urlAddress;
        this.device = values[0];
        this.latitude = values[1];
        this.longitude = values[2];
        this.height = values[3];
        this.speed = values[4];
        this.accuracy = values[5];
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void...params){
        return this.send();
    }

    @Override
    protected void onPostExecute(String response){
        super.onPostExecute(response);

        if (response != null){
            Log.v(TAG, response);
        }
    }

    private String send(){
        String arguments = new DataPackager(device, latitude, longitude, height, speed, accuracy).packData();

        HttpURLConnection conn = Connector.connect(urlAddress+"&"+arguments);
        if (conn == null) return null;

        try {
            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write("");
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer response = new StringBuffer();
                String line;

                while ((line=reader.readLine()) != null){
                    response.append(line);
                }
                reader.close();
                return response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
