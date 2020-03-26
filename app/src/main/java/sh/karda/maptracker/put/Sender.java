package sh.karda.maptracker.put;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import sh.karda.maptracker.dto.Positions;

public class Sender extends AsyncTask<Void, Void, String> {
    private final static String urlStr = "https://locationfunction.azurewebsites.net/api/LocationReceiver?code=bJ7eizF6A27F/g3/yblRcFUW3EYz0zAZavFHlL04/v6JN3W/6w410w==";
    private static final String TAG = "Sender";
    private Positions positions;

    public Sender(Positions positions){
        this.positions = positions;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void...params){
        try {
            return PutRequest.send(urlStr, positions);
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
