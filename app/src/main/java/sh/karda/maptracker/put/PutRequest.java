package sh.karda.maptracker.put;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import sh.karda.maptracker.dto.Positions;

public class PutRequest {
    private static String TAG = "PutRequest";

    public static String getJson(Positions positions){
        Gson gson = new Gson();
        String jsonString = gson.toJson(positions);
        Log.v(TAG, jsonString);
        return jsonString;
    }

    public static String send(String urlStr, Positions positions) throws IOException {
        String jsonString = getJson(positions);
        URL url = new URL(urlStr);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuilder stringBuffer = new StringBuilder();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(jsonString);
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                stringBuffer.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString;
    }
}
