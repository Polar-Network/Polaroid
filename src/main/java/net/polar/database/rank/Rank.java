package net.polar.database.rank;

import net.polar.utils.JsonUtils;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a rank object for a player
 */
public final class Rank {

    private final int weight;
    private final @NotNull String rankId;
    private final @NotNull String rankPrefix;
    private final @NotNull String nameColor;

    /**
     * Creates a new rank object
     * @param weight The weight of the rank, the lower the weight the higher the rank (so 0 is the highest)
     * @param rankId The id of the rank ("owner", "admin", "moderator", "default")
     * @param rankPrefix The prefix of the rank ("[Owner]", "[Admin]", "[Mod]", "")
     * @param nameColor The color of the player name ("<red>")
     */
    public Rank(
            int weight,
            @NotNull String rankId,
            @NotNull String rankPrefix,
            @NotNull String nameColor
    ) {
        this.weight = weight;
        this.rankId = rankId;
        this.rankPrefix = rankPrefix;
        this.nameColor = nameColor;
    }

    /**
     * Checks if this rank inherits from another rank, meaning that this rank weight is lower than the other rank
     * @param other The other rank
     * @return True if this rank inherits from the other rank, false otherwise
     */
    public boolean inherits(@NotNull Rank other) {
        return this.weight <= other.weight;
    }

    /**
     * @return The weight of the rank
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return The id of the rank
     */
    public @NotNull String getRankId() {
        return rankId;
    }

    /**
     * @return The prefix of the rank
     */
    public @NotNull String getRankPrefix() {
        return rankPrefix;
    }

    /**
     * @return The color of the player name
     */
    public @NotNull String getNameColor() {
        return nameColor;
    }


    /**
     * A utility method to convert a rank object to a BSON document for MongoDB
     * @param rank The rank object
     * @return The BSON document
     */
    public static @NotNull Document toDocument(@NotNull Rank rank) {
        String json = JsonUtils.GSON.toJson(rank);
        return Document.parse(json);
    }

    /**
     * A utility method to convert a BSON document to a rank object
     * @param document The BSON document
     * @return The rank object
     */
    public static @NotNull Rank fromDocument(@NotNull Document document) {
        String json = document.toJson();
        return JsonUtils.GSON.fromJson(json, Rank.class);
    }

}
