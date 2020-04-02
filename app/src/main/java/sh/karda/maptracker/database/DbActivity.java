package sh.karda.maptracker.database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import sh.karda.maptracker.R;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        ListView listView =  findViewById(R.id.list);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        List<PositionRow> rows = db.posDao().getLastDay(getDay(1), getDay(0));
        DbAdapter adapter = new DbAdapter(rows, getApplicationContext());
        listView.setAdapter(adapter);
    }

    private long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTimeInMillis();
    }
}
