package sh.karda.maptracker.get;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sh.karda.maptracker.dto.Point;
import sh.karda.maptracker.dto.Positions;

public class CustomDeserializer implements JsonDeserializer<Positions> {
    @Override
    public Positions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<Point> points = null;
        Gson gson = new Gson();
        Positions metaData = gson.fromJson(json, Positions.class);
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("altitude")) {
            JsonElement elem = jsonObject.get("altitude");

        }
        metaData.points = points;
        return metaData;
    }
}
