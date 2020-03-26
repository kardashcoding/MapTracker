package sh.karda.maptracker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PositionRow.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PositionDao posDao();
}
