package net.polar.launch;

import net.polar.Polaroid;
import org.jetbrains.annotations.NotNull;

public record LaunchArguments(
        boolean debugMode,
        @NotNull String address,
        int port,
        boolean onlineMode,
        @NotNull ProxySettings proxySettings
) {

    public static LaunchArguments parse(String[] args) {

        String address = "0.0.0.0";
        int port = 25565;
        boolean debugMode = false;
        boolean onlineMode = true;
        boolean proxyEnabled = false;
        String proxySecret = "";

        for (String arg : args) {
            if (isFlag(arg, "debug")) {
                debugMode = Boolean.parseBoolean(getFlagValue(arg, "debug"));
                continue;
            }
            if (isFlag(arg, "address")) {
                address = getFlagValue(arg, "address");
                continue;
            }
            if (isFlag(arg, "port")) {
                port = Integer.parseInt(getFlagValue(arg, "port"));
                continue;
            }
            if (isFlag(arg, "online-mode")) {
                onlineMode = Boolean.parseBoolean(getFlagValue(arg, "online-mode"));
                continue;
            }
            if (isFlag(arg, "proxy-enabled")) {
                proxyEnabled = Boolean.parseBoolean(getFlagValue(arg, "proxy-enabled"));
                continue;
            }
            if (isFlag(arg, "proxy-secret")) {
                proxySecret = getFlagValue(arg, "proxy-secret");
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

    private static String getFlagValue(String arg, String flag) {
        return arg.substring(arg.indexOf('=') + 1);
    }

}