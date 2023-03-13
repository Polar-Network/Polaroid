package net.polar.proxy;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.utils.validate.Check;
import net.polar.Polaroid;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

/**
 * Uses the database to keep track of all proxy servers on the network.
 */
public final class ProxyCountHandler {
    private ProxyCountHandler() {}

    private static final List<ProxyServer> proxyServers = Collections.synchronizedList(new ArrayList<>());

    /**
     * Initializes the proxy count handler.
     */
    public static void init(@NotNull Duration interval) {
        Check.stateCondition(!Polaroid.getProxySettings().enabled(), "Proxy count handler cannot be initialized when proxy mode is disabled");
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            MongoClient client = Polaroid.getDatabase().getClient();
            MongoDatabase database = client.getDatabase("network-data");
            MongoCollection<Document> collection = database.getCollection("proxy-servers");

            List<ProxyServer> proxyServers = new ArrayList<>();
            collection.find().forEach(document -> {
                String name = document.getString("name");
                List<UUID> connectedPlayers = document.getList("connected", UUID.class, List.of());
                proxyServers.add(new ProxyServer(name, Set.copyOf(connectedPlayers)));
            });
            ProxyCountHandler.proxyServers.clear();
            ProxyCountHandler.proxyServers.addAll(proxyServers);
        }).repeat(interval).executionType(ExecutionType.ASYNC).schedule();

    }

    /**
     * @return a list of all proxy servers on the network, will always be empty if the proxy mode is disabled and {@link #init()} has not been called.
     */
    public static @NotNull List<ProxyServer> getProxyServers() {
        return proxyServers;
    }


    static void addProxyServer(@NotNull ProxyServer proxyServer) {
        proxyServers.add(proxyServer);
    }

    static void addProxyServers(@NotNull List<ProxyServer> proxyServers) {
        ProxyCountHandler.proxyServers.addAll(proxyServers);
    }

}
