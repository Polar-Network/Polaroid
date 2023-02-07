package net.polar.launch;

import org.jetbrains.annotations.NotNull;

public record ProxySettings(
        boolean enabled,
        @NotNull String secret
) {
}
