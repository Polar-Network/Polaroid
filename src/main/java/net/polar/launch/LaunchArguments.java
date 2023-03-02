package net.polar.launch;

import net.polar.Polaroid;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the launch arguments for Polaroid.
 * @param debugMode Whether debug mode is enabled.
 * @param address The address to bind to.
 * @param port The port to bind to.
 * @param onlineMode Whether online mode is enabled.
 * @param mongoUri The MongoDB URI.
 * @param proxySettings The proxy settings. {@link ProxySettings}
 */
public record LaunchArguments(
        boolean debugMode,
        @NotNull String address,
        int port,
        boolean onlineMode,
        @NotNull String mongoUri,
        @NotNull ProxySettings proxySettings
) {

    /**
     * Parses launch arguments from the command line.
     * @return The parsed launch arguments.
     */
    public static LaunchArguments parse() {

        String address = System.getenv("POLAROID_ADDRESS");
        int port = Integer.parseInt(System.getenv("POLAROID_PORT"));
        boolean debugMode = Boolean.parseBoolean(System.getenv("POLAROID_DEBUG"));
        boolean onlineMode = Boolean.parseBoolean(System.getenv("POLAROID_ONLINE_MODE"));
        boolean proxyEnabled = Boolean.parseBoolean(System.getenv("POLAROID_PROXY_ENABLED"));
        String proxySecret = System.getenv("POLAROID_PROXY_SECRET");
        String mongoUri = System.getenv("POLAROID_MONGO_URI");

        if (address == null) address = "0.0.0.0";
        if (port == 0) port = 25565;

        if (proxyEnabled && proxySecret.isEmpty()) {
            Polaroid.getLogger().error("Proxy is enabled but no secret was provided. Please provide a secret using the --proxy-secret flag. Disabling proxy.");
            proxyEnabled = false;
        }
        if (onlineMode && proxyEnabled) {
            Polaroid.getLogger().error("Proxy is enabled but online mode is enabled. Please disable online mode using the --online-mode flag. Disabling Mojang authentication.");
            onlineMode = false;
        }
        return new LaunchArguments(
                debugMode, address, port, onlineMode, mongoUri, new ProxySettings(proxyEnabled, proxySecret)
        );
    }


}
