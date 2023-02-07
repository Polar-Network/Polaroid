package net.polar.launch;

import net.polar.Polaroid;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the launch arguments for Polaroid.
 * @param debugMode Whether debug mode is enabled.
 * @param address The address to bind to.
 * @param port The port to bind to.
 * @param onlineMode Whether online mode is enabled.
 * @param proxySettings The proxy settings. {@link ProxySettings}
 */
public record LaunchArguments(
        boolean debugMode,
        @NotNull String address,
        int port,
        boolean onlineMode,
        @NotNull ProxySettings proxySettings
) {

    /**
     * Parses launch arguments from the command line.
     * @param args The command line arguments.
     * @return The parsed launch arguments.
     */
    public static LaunchArguments parse(String[] args) {

        String address = "0.0.0.0";
        int port = 25565;
        boolean debugMode = false;
        boolean onlineMode = true;
        boolean proxyEnabled = false;
        String proxySecret = "";

        for (String arg : args) {
            if (isFlag(arg, "debug")) {
                debugMode = Boolean.parseBoolean(arg.replaceFirst("--debug=", ""));
                continue;
            }
            if (isFlag(arg, "address")) {
                address = arg.replaceFirst("--address=", "");
                continue;
            }
            if (isFlag(arg, "port")) {
                port = Integer.parseInt(arg.replaceFirst("--port=", ""));
                continue;
            }
            if (isFlag(arg, "online-mode")) {
                onlineMode = Boolean.parseBoolean(arg.replaceFirst("--online-mode=", ""));
                continue;
            }
            if (isFlag(arg, "proxy-enabled")) {
                proxyEnabled = Boolean.parseBoolean(arg.replaceFirst("--proxy-enabled=", ""));
                continue;
            }
            if (isFlag(arg, "proxy-secret")) {
                proxySecret = arg.replaceFirst("--proxy-secret=", "");
            }
        }
        if (proxyEnabled && proxySecret.isEmpty()) {
            Polaroid.getLogger().error("Proxy is enabled but no secret was provided. Please provide a secret using the --proxy-secret flag. Disabling proxy.");
            proxyEnabled = false;
        }
        if (onlineMode && proxyEnabled) {
            Polaroid.getLogger().error("Proxy is enabled but online mode is enabled. Please disable online mode using the --online-mode flag. Disabling Mojang authentication.");
            onlineMode = false;
        }
        return new LaunchArguments(
                debugMode, address, port, onlineMode, new ProxySettings(proxyEnabled, proxySecret)
        );
    }

    private static boolean isFlag(String arg, String flag) {
        return arg.equalsIgnoreCase("--" + flag + "=");
    }

}
