package sh.karda.maptracker.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PositionDao {
    @Query("SELECT * FROM PositionRow")
    List<PositionRow> getAllRows();

    @Insert
    void insertAll(PositionRow... rows);

    @Insert
    void insertRow(PositionRow row);
}
