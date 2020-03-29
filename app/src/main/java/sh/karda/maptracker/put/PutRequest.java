package sh.karda.maptracker.put;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.PositionRow;

public class PutRequest {
    private static String TAG = "PutRequest";
    private static int itemsSent;
    public static String getJson(AppDatabase db){
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();

        List<PositionRow> items = db.posDao().getAllUnsent();
        itemsSent = items.size();
        String jsonString = gson.toJson(items);
        Log.v(TAG, jsonString);
        return jsonString;
    }

    public static String send(String urlStr, AppDatabase db) throws IOException {
        Log.v(TAG, "Shit kom hit 1");

        String jsonString = getJson(db);
        Log.v(TAG, "Shit Json: " + jsonString);
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
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                stringBuffer.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.v(TAG, uc.getResponseMessage());
        if (uc.getResponseCode() == 200){
            db.posDao().setRowsAsSent(true);
            Log.v(TAG, "Shit sendte: " + itemsSent);
            Log.v(TAG, "Resultatet var: " + br);
        }
        uc.disconnect();
        return jsonString;
    }
}
