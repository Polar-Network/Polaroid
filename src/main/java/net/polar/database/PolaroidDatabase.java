package net.polar.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;

/**
 * PolaroidDatabase, this includes the MongoDb client
 */
public class PolaroidDatabase {

    private final MongoClient client;

    /**
     * Create a new PolaroidDatabase instance.
     * @param client The MongoDb client.
     */
    public PolaroidDatabase(@NotNull MongoClient client) {
        this.client = client;
    }

    /**
     * Create a new PolaroidDatabase instance.
     * @param mongoUri The MongoDb URI.
     */
    public PolaroidDatabase(@NotNull String mongoUri) {
        this.client = MongoClients.create(mongoUri);
    }

    /**
     * @return The MongoDb client.
     */
    public @NotNull MongoClient getClient() {
        return client;
    }

}
