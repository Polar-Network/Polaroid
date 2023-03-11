package net.polar.database.rank;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import net.polar.Polaroid;
import net.polar.database.PolaroidDatabase;
import net.polar.entity.player.PolaroidPlayer;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

class RankProviderImpl implements RankProvider {

    @Override
    @NotNull
    public Rank provide(@NotNull PolaroidPlayer player) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        PolaroidDatabase db = Polaroid.getDatabase();
        MongoDatabase ranks = db.getClient().getDatabase("rank");
        MongoCollection<Document> players = ranks.getCollection("players");

        Document document = players.find(new Document("player", username)).first();
        if (document == null) {
            return returnDefault(players, username);
        }

        MongoCollection<Document> ranksCollection = ranks.getCollection("ranks");
        Document rankDocument = ranksCollection.find(new Document("rank-id", document.getString("rank"))).first();
        if (rankDocument == null) {
            return returnDefault(players, username);
        }

        return Rank.fromDocument(rankDocument);
    }

    @Override
    @NotNull
    public Rank getDefault() {
        MongoCollection<Document> ranks = Polaroid.getDatabase().getClient().getDatabase("groups").getCollection("ranks");
        if (ranks.find().first() == null) {
            throw new IllegalStateException("No ranks found in database");
        }
        FindIterable<Document> documents = ranks.find();
        int lastWeight = 0;
        Document lastDocument = null;
        for (Document document : documents) {
            int weight = document.getInteger("weight");
            if (weight > lastWeight) {
                lastWeight = weight;
                lastDocument = document;
            }
        }
        if (lastDocument == null) {
            throw new IllegalStateException("No ranks found in database");
        }
        return Rank.fromDocument(lastDocument);
    }

    @Override
    public void appendRank(@NotNull Rank rank) {
        MongoCollection<Document> ranks = Polaroid.getDatabase().getClient().getDatabase("groups").getCollection("ranks");
        ranks.insertOne(Rank.toDocument(rank));
    }

    private @NotNull Rank returnDefault(MongoCollection<Document> players, String username) {
        Rank rank = getDefault();
        players.updateOne(new Document("player", username), new Document("player", username).append("rank", rank.getRankId()), new UpdateOptions().upsert(true));
        return rank;
    }

}
