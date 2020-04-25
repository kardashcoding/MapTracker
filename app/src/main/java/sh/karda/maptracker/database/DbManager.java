package sh.karda.maptracker.database;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.room.Room;
import sh.karda.maptracker.MapsActivity;

public class DbManager {
    private static String TAG = "DbManager";
    private static AppDatabase db;
    public static void Insert(final PositionRow row){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    getDbInstance().posDao().insertRow(row);
                }});
            t.start();
    }

    public static void Delete(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.GERMAN); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());
                getDbInstance().posDao().deleteAllRows(nowAsISO);
            }});
        t.start();
    }

    public static void SetRowAsSent(final ArrayList<String> guid){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = getDbInstance().posDao().setRowsAsSent(guid);
                Log.v(TAG, "Set " + i + " rows as sent");
            }});
        t.start();
    }

    public static AppDatabase getDbInstance(){
        if (db != null) return db;
        return db = Room.databaseBuilder(MapsActivity.getAppContext(), AppDatabase.class, "location")
                .addMigrations(Migrations.MIGRATION_4_5)
                .allowMainThreadQueries()
                .build();
    }

    public static void InsertAndWait(final PositionRow row) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getDbInstance().posDao().insertRow(row);
            }});
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void MarkAllAsUnsent() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getDbInstance().posDao().markAsUnsent();
            }});
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long getYesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTimeInMillis();
    }

    public static long getMonthAgo() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -60);
        return cal.getTimeInMillis();
    }

    public static long getToday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        return cal.getTimeInMillis();
    }
}
