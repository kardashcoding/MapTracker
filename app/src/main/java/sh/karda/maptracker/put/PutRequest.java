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
import sh.karda.maptracker.database.DbManager;
import sh.karda.maptracker.database.PositionRow;

class PutRequest {
    private static String TAG = "PutRequest";
    private static int itemsSent;

    private static String getJsonFromDb(){
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();

        List<PositionRow> items = DbManager.getDbInstance().posDao().getAllUnsent(DbManager.getYesterday(), DbManager.getToday());
        if (items.size() == 0) return "";

        itemsSent = items.size();
        String jsonString = gson.toJson(items);
        Log.v(TAG, jsonString);
        return jsonString;
    }

    static String send(String urlStr) throws IOException {
        Log.v(TAG, "Shit kom hit 1");
        String jsonString = getJsonFromDb();
        if (jsonString.equals("")) return null;
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
        BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

        StringBuilder sb = new StringBuilder();

        String l;
        while ((l = br.readLine()) != null) {
            sb.append(l).append("\n");
        }
        br.close();

        String response = sb.toString();
        Log.v(TAG, "Response code: " + uc.getResponseCode());

        Log.v(TAG, uc.getResponseMessage());
        if (uc.getResponseCode() == 200){
            Log.v(TAG, "Shit sendte: " + itemsSent);
            Log.v(TAG, "Resultatet var: " + br);
        }
        uc.disconnect();
        return response;
    }

    static String delete(String urlStr, String deviceId) {
        String result = "";

        try {
            String deleteUrl = urlStr + "&device=" + deviceId;
            URL url = new URL(deleteUrl);
            Log.v(TAG, deleteUrl);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestMethod("DELETE");
            uc.setDoInput(true);
            uc.setInstanceFollowRedirects(false);
            uc.connect();
            OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), StandardCharsets.UTF_8);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String l;
            while ((l = br.readLine()) != null) {
                sb.append(l).append("\n");
            }
            br.close();

            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
