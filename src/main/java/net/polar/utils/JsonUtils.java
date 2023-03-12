package net.polar.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Json utility methods to serialize, deserialize, write and read json files among other things.
 */
public final class JsonUtils {


    /**
     * The internally used {@link Gson} instance, this is public so that other code can build on top of it.
     */
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .create();

    private JsonUtils() {}


    /**
     * Creates a new {@link JsonReader} from the given {@link File}.
     * @param file the file to read from
     * @return the created {@link JsonReader}
     * @throws RuntimeException if the file could not be found
     */
    public static @NotNull JsonReader newJsonReader(@NotNull File file) {
        try {
            return new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new {@link JsonWriter} from the given {@link File}.
     * @param file the file to write to
     * @return the created {@link JsonWriter}
     * @throws RuntimeException if the file could not be found
     */
    public static @NotNull JsonWriter newJsonWriter(@NotNull File file) {
        try {
            return new JsonWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a serialized json string to the given {@link File}.
     * @param src the object to serialize
     * @param typeOfSrc the type of the object
     * @param file the file to write to
     * @throws RuntimeException if there is an error writing to the file
     */
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

    /**
     * Reads a serialized json string from the given {@link File}.
     * @param file the file to read from
     * @param typeOfT the type of the object
     * @return the deserialized object
     * @throws RuntimeException if there is an error reading from the file
     * @param <T> the type of the object
     */
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

    /**
     * Pretty prints the given {@link Document} to the given {@link Logger} or to {@link System#out} if the logger is null.
     * @param document the document to print
     * @param logger the logger to print to
     */
    public static void prettyPrint(@NotNull Document document, @Nullable Logger logger) {
        if (logger == null) {
            System.out.println(GSON.toJson(document));
        }
        else {
            logger.info(GSON.toJson(document));
        }
    }

}
