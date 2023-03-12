package net.polar.database.rank;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import net.polar.Polaroid;
import net.polar.entity.player.PolaroidPlayer;
import net.polar.utils.chat.ChatColor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class RankProviderImpl implements RankProvider {

    private final Set<Rank> ranks = ConcurrentHashMap.newKeySet();
    private Rank defaultRank;
    private final MongoDatabase database = Polaroid.getDatabase().getClient().getDatabase("rank");
    private final MongoCollection<Document> ranksCollection = database.getCollection("ranks");

    @Override
    public @NotNull Set<Rank> getAvailableRanks() {
        return this.ranks;
    }

    @Override
    @NotNull
    public Rank provide(@NotNull PolaroidPlayer player) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        MongoCollection<Document> players = database.getCollection("players");
        Document document = players.find(new Document("player", username)).first();
        if (document == null || document.getString("rank") == null) {
            return defaultRank;
        }
        return this.ranks.stream().filter(rank -> rank.getRankId().equals(document.getString("rank"))).findFirst().orElse(defaultRank);
    }


    @Override @NotNull
    public Rank getDefault() {
        return defaultRank;
    }

    @Override
    public void appendRank(@NotNull Rank rank) {
        this.ranks.add(rank);
        this.ranksCollection.updateOne(
                new Document("rank-id", rank.getRankId()),
                Rank.toDocument(rank),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void refreshRanks() {
        this.ranks.clear();
        FindIterable<Document> documents = ranksCollection.find();
        int lastWeight = 0;
        Rank defaultRank = documents.first() == null ? null : Rank.fromDocument(documents.first());
        for (Document document : documents) {

            Rank rank = Rank.fromDocument(document);
            if (rank.getWeight() > lastWeight) {
                lastWeight = rank.getWeight();
                defaultRank = rank;
            }
            this.ranks.add(rank);
            Polaroid.getLogger().info(
                    ChatColor.color(
                            "<green>Loaded rank <white>" + rank.getRankId() + "<green> with weight <white>" + rank.getWeight() + "<green> and prefix <white>" + rank.getRankPrefix()
                    )
            );
        }
        this.defaultRank = defaultRank;
        Polaroid.getLogger().info(ChatColor.color("<green>Loaded <white>" + this.ranks.size() + "<green> ranks. Default rank is <white>" + defaultRank.getRankId() + "<green>."));
    }

    @Override
    public void saveAll() {
        this.ranks.forEach(rank -> {
            this.ranksCollection.updateOne(
                    new Document("rank-id", rank.getRankId()),
                    Rank.toDocument(rank),
                    new UpdateOptions().upsert(true)
            );
        });
    }

    @Override
    public void save(@NotNull PolaroidPlayer player) {
        String username = Polaroid.isOnlineMode() ? player.getUuid().toString() : player.getUsername();
        MongoCollection<Document> players = database.getCollection("players");

        Document document = players.find(new Document("player", username)).first();
        if (document == null) {
            players.insertOne(new Document("player", username).append("rank", player.getRank().getRankId()));
            return;
        }
        players.replaceOne(new Document("player", username), new Document("player", username).append("rank", player.getRank().getRankId()));
    }

}
