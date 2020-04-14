package sh.karda.maptracker.database;

import java.util.ArrayList;
import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PositionDao {
    @Query("SELECT * FROM PositionRow")
    List<PositionRow> getAllRows();

    @Query("SELECT * FROM PositionRow WHERE sent = 0")
    List<PositionRow> getAllUnsent();

    @Insert
    void insertAll(PositionRow... rows);

    @Insert
    void insertRow(PositionRow row);

    @Query("UPDATE PositionRow SET sent = :s")
    void setRowsAsSent(boolean s);

    @Query("UPDATE PositionRow SET guid = guid+id")
    void messUpGuid();

    @Query("SELECT COUNT(*) FROM PositionRow WHERE sent = 0")
    int unsentRows();

    @Query("SELECT * FROM positionrow WHERE deleted is NULL and date BETWEEN :from AND :to")
    List<PositionRow> getLastDay(long from, long to);

    @Query("UPDATE PositionRow SET sent = 1 WHERE guid = :s")
    void setRowsAsSent(String s);

    @Query("UPDATE PositionRow SET deleted = :now, sent = 0")
    void deleteAllRows(String now);

    @Query("UPDATE PositionRow SET deleted = NULL, sent = 0")
    void markAsUnsent();
}
