package sh.karda.maptracker.database;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import sh.karda.maptracker.R;
import android.os.Bundle;
import java.util.Calendar;
import java.util.List;

public class DbActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        recyclerView =  findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "location")
                .allowMainThreadQueries()
                .addMigrations(Migrations.MIGRATION_4_5)
                .addMigrations(Migrations.MIGRATION_5_6)
                .build();
        List<PositionRow> rows = db.posDao().getLastDay(getDay(1), getDay(0));
        if (rows == null || rows.size() == 0) return;
        adapter = new DbAdapter(rows);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private long getDay(int i) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal.getTimeInMillis();
    }
}
