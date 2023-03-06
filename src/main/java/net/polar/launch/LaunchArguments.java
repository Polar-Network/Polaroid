package net.polar.launch;

import net.polar.Polaroid;
import net.polar.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
    private final ProxySettings proxySettings;

    /**
     * Creates a new launch arguments instance.
     * @param host The host to bind to.
     * @param mongoUri The MongoDB URI.
     * @param onlineMode If online mode is enabled.
     * @param maxPlayers The maximum amount of players.
     * @param debug If debug mode is enabled.
     * @param proxySettings The proxy settings.
     */
    public LaunchArguments(
            @NotNull String host,
            @NotNull String mongoUri,
            boolean onlineMode,
            int maxPlayers,
            boolean debug,
            @NotNull ProxySettings proxySettings
    ) {
        this.host = host;
        this.mongoUri = mongoUri;
        this.onlineMode = onlineMode;
        this.maxPlayers = maxPlayers;
        this.debug = debug;
        this.proxySettings = proxySettings;
    }

    /**
     * Creates a new launch arguments instance with default values.
     */
    public LaunchArguments() {
        this(
            "0.0.0.0:25565",
            "mongodb://localhost:27017",
             true, 20, false, new ProxySettings(false, "")
        );
    }


    /**
     * Parses launch arguments from the default config file.
     * @return The parsed launch arguments.
     */
    public static LaunchArguments defaults() {
        final File file = new File(Polaroid.getLocalPath().toFile(), "config.json");
        if (file.exists() && file.isFile()) {
            return JsonUtils.read(file, LaunchArguments.class);
        }
        LaunchArguments args = new LaunchArguments(); // Default arguments
        JsonUtils.prettyWrite(args, LaunchArguments.class, file);
        return args;
    }

    /**
     * @return the connection host. This contains both the IP and the port (ex: 0.0.0.0:25565)
     */
    public String host() {
        return host;
    }

    /**
     * @return the MongoDB connection URI.
     */
    public String mongoUri() {
        return mongoUri;
    }

    /**
     * @return if online mode is enabled.
     */
    public boolean onlineMode() {
        return onlineMode;
    }

    /**
     * @return the maximum amount of players.
     */
    public int maxPlayers() {
        return maxPlayers;
    }

    /**
     * @return if debug mode is enabled.
     */
    public boolean debug() {
        return debug;
    }

    /**
     * @return the proxy settings.
     */
    public ProxySettings proxySettings() {
        return proxySettings;
    }
}
