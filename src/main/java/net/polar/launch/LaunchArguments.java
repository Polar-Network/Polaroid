package net.polar.launch;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.polar.Polaroid;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

/**
 * Launch Arguments used to start the server.
 * This class is not a record due to Gson having issues with records.
 */
public class LaunchArguments {

    private final String host;
    private final String mongoUri;
    private final boolean onlineMode;
    private final int maxPlayers;
    private final boolean debug;
    private final List<String> motd;
    private final ProxySettings proxySettings;

    /**
     * Creates a new launch arguments instance.
     * @param host The host to bind to.
     * @param mongoUri The MongoDB URI.
     * @param onlineMode If online mode is enabled.
     * @param maxPlayers The maximum amount of players.
     * @param debug If debug mode is enabled.
     * @param motd The MOTDs for the server.
     * @param proxySettings The proxy settings.
     */
    public LaunchArguments(
            @NotNull String host,
            @NotNull String mongoUri,
            boolean onlineMode,
            int maxPlayers,
            boolean debug,
            @NotNull List<String> motd,
            @NotNull ProxySettings proxySettings
    ) {
        this.host = host;
        this.mongoUri = mongoUri;
        this.onlineMode = onlineMode;
        this.maxPlayers = maxPlayers;
        this.debug = debug;
        this.motd = motd;
        this.proxySettings = proxySettings;
    }

    /**
     * Creates a new launch arguments instance with default values.
     */
    public LaunchArguments() {
        this(
            "0.0.0.0:25565",
            "mongodb://localhost:27017",
             true, 20, false, List.of(), new ProxySettings(false, "")
        );
    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();

    /**
     * Parses launch arguments from the default config file.
     * @return The parsed launch arguments.
     */
    public static LaunchArguments parse() throws IOException {
        final File file = new File(Polaroid.getLocalPath().toFile(), "config.json");
        if (file.exists()) {
            return GSON.fromJson(new JsonReader(new FileReader(file)), LaunchArguments.class);
        }
        LaunchArguments args = new LaunchArguments(); // Default arguments
        JsonWriter writer = new JsonWriter(new FileWriter(file));
        writer.setLenient(true);
        writer.setIndent("  ");
        GSON.toJson(args, LaunchArguments.class, writer);
        writer.close();
        return args;
    }

    public String host() {
        return host;
    }

    public String mongoUri() {
        return mongoUri;
    }

    public boolean onlineMode() {
        return onlineMode;
    }

    public int maxPlayers() {
        return maxPlayers;
    }

    public boolean debug() {
        return debug;
    }

    public List<String> motd() {
        return motd;
    }

    public ProxySettings proxySettings() {
        return proxySettings;
    }
}
