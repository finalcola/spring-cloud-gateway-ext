package org.finalcola.gateway.ext.common.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;

/**
 * @author: finalcola
 * @date: 2023/3/12 22:11
 */
public class JsonUtils {

    private static final Gson gson = new Gson();

    @Nullable
    public static String toJson(@Nullable Object obj) {
        return gson.toJson(obj);
    }

    @Nullable
    public static <T> T fromJson(@Nullable String str, Class<T> klass) {
        return gson.fromJson(str, klass);
    }

    @Nullable
    public static <T> T fromJson(@Nullable String str, TypeToken<T> typeToken) {
        return gson.fromJson(str, typeToken);
    }
}
