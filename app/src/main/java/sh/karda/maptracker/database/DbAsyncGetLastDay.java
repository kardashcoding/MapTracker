package sh.karda.maptracker.database;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.util.Calendar;
import java.util.List;

import javax.xml.validation.Validator;

import sh.karda.maptracker.Variables;
import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;
import sh.karda.maptracker.map.MapHelper;


public class DbAsyncGetLastDay extends AsyncTask<Void, Void, Positions> {

    private GoogleMap map;
    Positions points;

    public DbAsyncGetLastDay(GoogleMap map) {
        this.map = map;

    }

    @Override
    protected Positions doInBackground(Void... voids) {
        List<PositionRow> rows = DbManager.getDbInstance().posDao().getLastDay(Variables.getFromTime(), Variables.getNow());
        points = new Positions();
        for (PositionRow row: rows) {
            points.points.add(new Point(row.getAccuracy(), row.isConnectedToWifi(), row.getDate(), row.getDevice(), row.getGuid(), row.getHeight(), row.getId(), row.getLatitude(), row.getLongitude(), row.getSpeed(), row.getWifi(), row.getDeleted()));
        }
        return points;
    }

    private static long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTimeInMillis();
    }
    @Override
    protected void onPostExecute(Positions points) {
        super.onPostExecute(points);
        MapHelper.addToMap(map, points);
    }

}
