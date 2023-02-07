package net.polar.launch;

import org.jetbrains.annotations.NotNull;

/**
 * Proxy Settings used in {@link LaunchArguments}
 * @param enabled if proxy is enabled
 * @param secret proxy secret
 */
public record ProxySettings(
        boolean enabled,
        @NotNull String secret
) {
}
