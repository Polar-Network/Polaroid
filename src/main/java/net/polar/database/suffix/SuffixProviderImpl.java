package net.polar.database.suffix;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import net.polar.Polaroid;
import net.polar.database.PolaroidDatabase;
import net.polar.entity.player.PolaroidPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class SuffixProviderImpl implements SuffixProvider {


    @Override
    public @Nullable Suffix getActiveSuffix(@NotNull PolaroidPlayer player) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        PolaroidDatabase db = Polaroid.getDatabase();
        MongoDatabase suffixes = db.getClient().getDatabase("suffix");
        MongoCollection<Document> players = suffixes.getCollection("players");
        Document document = players.find(new Document("player", username)).first();
        if (document == null) {
            players.updateOne(
                    new Document("player", username),
                    new Document("player", username)
                            .append("available", new ArrayList<>(0))
                            .append("active", null),
                    new UpdateOptions().upsert(true)
            );
            return null;
        }

        String suffixId = document.getString("active");
        if (suffixId == null) {
            return null;
        }
        MongoCollection<Document> suffixesCollection = suffixes.getCollection("suffixes");
        Document suffixDocument = suffixesCollection.find(new Document("suffix-id", suffixId)).first();
        if (suffixDocument == null) {
            return null;
        }
        return Suffix.fromDocument(suffixDocument);
    }

    @Override
    public void giveSuffix(@NotNull PolaroidPlayer player, @NotNull Suffix suffix) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        PolaroidDatabase db = Polaroid.getDatabase();
        MongoDatabase suffixes = db.getClient().getDatabase("suffix");
        MongoCollection<Document> players = suffixes.getCollection("players");
        players.updateOne(
                new Document("player", username),
                new Document("$addToSet", new Document("available", suffix.getSuffixId())),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public List<Suffix> getSuffixes(@NotNull PolaroidPlayer player) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        PolaroidDatabase db = Polaroid.getDatabase();
        MongoDatabase suffixes = db.getClient().getDatabase("suffix");
        MongoCollection<Document> players = suffixes.getCollection("players");

        Document document = players.find(new Document("player", username)).first();
        if (document == null) {
            players.updateOne(
                    new Document("player", username),
                    new Document("player", username)
                            .append("available", new ArrayList<>(0))
                            .append("active", null),
                    new UpdateOptions().upsert(true)
            );
            return new ArrayList<>(0);
        }

        List<Suffix> suffixesList = new ArrayList<>();
        for (String suffixId : document.getList("available", String.class)) {
            MongoCollection<Document> suffixesCollection = suffixes.getCollection("suffixes");
            Document suffixDocument = suffixesCollection.find(new Document("suffix-id", suffixId)).first();
            if (suffixDocument == null) {
                continue;
            }
            suffixesList.add(Suffix.fromDocument(suffixDocument));
        }
        return suffixesList;
    }

}
