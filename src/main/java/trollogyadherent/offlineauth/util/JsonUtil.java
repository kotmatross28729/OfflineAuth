package trollogyadherent.offlineauth.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    public static String objectToJson(Object o) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(o).replace("\n", "");
    }

    public static Object jsonToObject(String json, Class class_) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.fromJson(json, class_);
    }

    public static String objectToJsonList (Object[] o) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(o);
    }

    public static Object[] jsonToObjectList (String json, Class class_) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return (Object[]) gson.fromJson(json, class_);
    }
}
