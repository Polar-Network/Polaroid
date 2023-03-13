package net.polar.proxy;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a proxy server. This class is immutable. All fields are final.
 */
public class ProxyServer {

    private final String name;
    private final Set<UUID> connectedPlayers;

    ProxyServer(
            @NotNull String name,
            @NotNull Set<UUID> connectedPlayers
    ){
        this.name = name;
        this.connectedPlayers = connectedPlayers;
    }

    /**
     * @return The name of the proxy server.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return The amount of online players on the proxy server.
     */
    public int getOnlinePlayers() {
        return connectedPlayers.size();
    }

    /**
     * @return the name of the proxy server with the first letter capitalized for display purposes.
     */
    public @NotNull String getNameCapitalized() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * @return the set of connected players on the proxy server.
     */
    public @NotNull Set<UUID> getConnectedPlayers() {
        return connectedPlayers;
    }

}
