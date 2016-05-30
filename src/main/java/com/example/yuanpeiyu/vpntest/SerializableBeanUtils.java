package com.example.yuanpeiyu.vpntest;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 *
 */
public class SerializableBeanUtils {

    private static final String TAG = "SerializableBeanUtils";

    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializerUtil())
            .serializeNulls().create();

    public static final class DateSerializerUtil implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.getTime());
        }

        @Override
        public Date deserialize(JsonElement element, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return new Date(element.getAsJsonPrimitive().getAsLong());
        }

    }

    /**
     * util method to convert a java object to gson string's bytes
     *
     * @param obj java object
     * @param <T> object type
     * @return accordant obj's gson string's bytes
     */
    public static <T> byte[] convertObjectToGsonStringBinary(T obj) {
        synchronized (gson) {
            return gson.toJson(obj).getBytes();
        }
    }

    /**
     * util method to convert a bytes array to a java object
     *
     * @param bytes gson string's bytes
     * @param <T>   target java object's type
     * @param clz   class of target java object
     * @return target java object
     */
    public static <T> T convertGsonStringBinaryToObject(byte[] bytes, Class<T> clz) {
        synchronized (gson) {
            return gson.fromJson(new String(bytes), clz);
        }
    }
}
