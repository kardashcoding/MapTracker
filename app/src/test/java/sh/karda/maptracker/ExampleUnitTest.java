package sh.karda.maptracker;

import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;

import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;
import sh.karda.maptracker.get.PositionPoints;
import sh.karda.maptracker.put.PutRequest;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_json() {
        Positions positions = new Positions();
        Point p1 = new Point();
        String d = Instant.now().toString();
        p1.speed = 1;
        p1.longitude = 2;
        p1.latitude = 3;
        p1.height = 4;
        p1.date = Calendar.getInstance().getTime().toString();
        p1.device = "d";
        Point p2 = new Point();
        p2.speed = 1;
        p2.longitude = 2;
        p2.latitude = 3;
        p2.height = 4;
        p2.date = Calendar.getInstance().getTime().toString();
        p2.device = "d";
        positions.points.add(p1);
        positions.points.add(p2);
        String json = getJson(positions);
        assertNotNull(json);
        Gson gson = new Gson();

        PositionPoints dataItems = gson.fromJson(json, PositionPoints.class);
        assertNotNull(dataItems);
    }

    @Test
    public void test_gson(){
        String json = "{\"items\":[{\"id\":1,\"device\":\"meg\",\"longitude\":3,\"latitude\":4,\"accuracy\":6,\"height\":2,\"speed\":5,\"date\":\"2020-03-25T14:21:23.493\"},{\"id\":2,\"device\":\"meg\",\"longitude\":3,\"latitude\":4,\"accuracy\":6,\"height\":2,\"speed\":5,\"date\":\"2020-03-25T14:22:17.953\"},{\"id\":3,\"device\":\"d\",\"longitude\":2,\"latitude\":3,\"accuracy\":0,\"height\":4,\"speed\":1,\"date\":\"2020-03-26T06:07:33\"},{\"id\":4,\"device\":\"d\",\"longitude\":2,\"latitude\":3,\"accuracy\":0,\"height\":4,\"speed\":1,\"date\":\"2020-03-26T06:07:33\"}]}\n";
        Gson gson = new Gson();
        PositionPoints dataItems = gson.fromJson(json, PositionPoints.class);
        assertNotNull(dataItems);
    }

    public static String getJson(Positions positions){
        Gson gson = new Gson();
        String jsonString = gson.toJson(positions);
        return jsonString;
    }
}

