package net.polar.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class PolaroidDatabase {

    private final MongoClient client;

    public PolaroidDatabase(MongoClient client) {
        this.client = client;
    }

    public PolaroidDatabase(String mongoUri) {
        this.client = MongoClients.create(mongoUri);
    }

    public MongoClient getClient() {
        return client;
    }

}
