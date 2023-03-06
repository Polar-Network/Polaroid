package net.polar.launch;

import org.jetbrains.annotations.NotNull;

/**
 * Proxy Settings used in {@link LaunchArguments}
 * This class is not a record due to Gson having issues with records.
 */
public class ProxySettings {

    private final boolean enabled;
    private final String secret;

    /**
     * Creates a new proxy settings instance.
     * @param enabled If the proxy is enabled.
     * @param secret The secret used to authenticate with the proxy. This is always blank if the proxy is disabled.
     */
    public ProxySettings(boolean enabled, @NotNull String secret) {
        this.enabled = enabled;
        this.secret = secret;
    }

    public boolean enabled() {
        return enabled;
    }

    public String secret() {
        return secret;
    }
}
