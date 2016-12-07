package cn.clickwise.utils.other;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by T420s on 2016/11/9.
 */
public class JsonUtil {
    public static String toJson(Map<?, ?> mapJson) {
        return new Gson().toJson(mapJson);
    }

    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T toBean(Class<T> type, String jsonStr) {
        if (jsonStr == null) return null;
        Gson gson = new Gson();
        T bean = gson.fromJson(jsonStr, type);
        return bean;
    }

    public static <T> T toBean(Class<T> type, byte[] bytes) {
        if (bytes == null) return null;
        Gson gson = new Gson();
        T bean = gson.fromJson(new String(bytes), type);
        return bean;
    }
}
