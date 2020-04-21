package sh.karda.maptracker.database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import sh.karda.maptracker.R;

import android.os.Bundle;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;

public class DbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        ListView listView =  findViewById(R.id.list);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .addMigrations(Migrations.MIGRATION_4_5)
                .build();
        List<PositionRow> rows = db.posDao().getLastDay(getDay(1), getDay(0));
        if (rows == null || rows.size() == 0) return;
        DbAdapter adapter = new DbAdapter(rows, getApplicationContext());
        listView.setAdapter(adapter);
    }

    private long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTimeInMillis();
    }
}
