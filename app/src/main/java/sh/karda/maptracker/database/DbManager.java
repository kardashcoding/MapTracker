package sh.karda.maptracker.database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.room.Room;
import sh.karda.maptracker.MapsActivity;

public class DbManager {
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
                LocalDate d = LocalDate.now();
                String now = DateTimeFormatter.ISO_DATE_TIME.format(d);
                getDbInstance().posDao().deleteAllRows(now);
            }});
        t.start();
    }

    public static void SetRowsAsSent(final String guid){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getDbInstance().posDao().setRowsAsSent(guid);
            }});
        t.start();
    }

    public static AppDatabase getDbInstance(){
        if (db != null) return db;
        return db = Room.databaseBuilder(MapsActivity.getAppContext(), AppDatabase.class, "production")
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
}
