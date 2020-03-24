package sh.karda.maptracker;

import org.junit.Test;

import sh.karda.maptracker.get.Point;

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
    public void test_date(){
        Point p = new Point();
        p.date = "2020-03-23T19:09:32.573";
        String dateString = p.getTitle();
        assertEquals("19:09:32", dateString);
    }
}