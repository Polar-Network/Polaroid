package net.polar.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;

public final class JsonUtils {

    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .create();

    private JsonUtils() {}

    public static @NotNull JsonReader newJsonReader(@NotNull File file) {
        try {
            return new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull JsonWriter newJsonWriter(@NotNull File file) {
        try {
            return new JsonWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(@NotNull Object src, @NotNull Type typeOfSrc, @NotNull File file) {
        try {
            JsonWriter writer = newJsonWriter(file);
            GSON.toJson(src, typeOfSrc, writer);
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(@NotNull File file, @NotNull Type typeOfT) {
        JsonReader reader = newJsonReader(file);
        T result = GSON.fromJson(reader, typeOfT);
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
