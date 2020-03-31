package sh.karda.maptracker;

import com.google.gson.Gson;

import org.junit.Test;

import sh.karda.maptracker.dto.Positions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    }

    @Test
    public void test_gson(){
        Gson gson = new Gson();
        Positions result  = gson.fromJson(shortJson, Positions.class);
        assertNotNull(result);
    }

    private String shortJson = "{\n" +
            "    \"points\": [\n" +
            "        {\n" +
            "            \"accuracy\": 1493,\n" +
            "            \"connected_to_wifi\": false,\n" +
            "            \"date\": \"2020-03-29T10:04:06.293\",\n" +
            "            \"device\": \"c5b9976c51f027fc\",\n" +
            "            \"guid\": \"7a82e28f-1ef3-4b47-adc9-df0f6abd1e27\",\n" +
            "            \"height\": 0,\n" +
            "            \"id\": 42,\n" +
            "            \"latitude\": 37.421997,\n" +
            "            \"longitude\": -122.084,\n" +
            "            \"speed\": 0,\n" +
            "            \"wifi\": \"\\\"AndroidWifi\\\"\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public static String getJson(){
        return "{\n" +
                "    \"name\": \"Anders\",\n" +
                "    \"age\":88,     \n" +
                "    \"kids\":[{\n" +
                "        \"k1\":\"Emma\",\n" +
                "        \"k2\":66\n" +
                "    },   \n" +
                "    {\n" +
                "        \"k1\":\"Emma\",\n" +
                "        \"k2\":66\n" +
                "    }]\n" +
                "}";

    }
    private String longJson = "{\n" +
            "    \"points\": [\n" +
            "        {\n" +
            "            \"accuracy\": 110.0,\n" +
            "            \"connected_to_wifi\": false,\n" +
            "            \"date\": \"2020-03-28T14:02:04.663\",\n" +
            "            \"device\": \"a39d7fd623d0afb0\",\n" +
            "            \"guid\": \"982f7f2d-80f1-495a-889a-005faeaef694\",\n" +
            "            \"height\": 0.0,\n" +
            "            \"id\": 1,\n" +
            "            \"latitude\": 59.93168,\n" +
            "            \"longitude\": 10.617213,\n" +
            "            \"speed\": 0.0,\n" +
            "            \"wifi\": \"<unknown ssid>\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"accuracy\": 20.0,\n" +
            "            \"connected_to_wifi\": false,\n" +
            "            \"date\": \"2020-03-28T18:53:57.37\",\n" +
            "            \"device\": \"14e919d1e3dfdfff\",\n" +
            "            \"guid\": \"79967\",\n" +
            "            \"height\": 144.0,\n" +
            "            \"id\": 2,\n" +
            "            \"latitude\": 59.93168,\n" +
            "            \"longitude\": 10.617213,\n" +
            "            \"speed\": 0.0,\n" +
            "            \"wifi\": \"\\\"Dr. Alban\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"accuracy\": 20.0,\n" +
            "            \"connected_to_wifi\": false,\n" +
            "            \"date\": \"2020-03-28T18:55:53.727\",\n" +
            "            \"device\": \"14e919d1e3dfdfff\",\n" +
            "            \"guid\": \"542\",\n" +
            "            \"height\": 144.0,\n" +
            "            \"id\": 3,\n" +
            "            \"latitude\": 59.93168,\n" +
            "            \"longitude\": 10.617213,\n" +
            "            \"speed\": 0.0,\n" +
            "            \"wifi\": \"\\\"Dr. Alban\\\"\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
class a{
    String name;
    int age;
    b[] kids;
}

class b{
    String k1;
    int k2;
}

