package net.polar.database.suffix;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a suffix object for a player
 */
public final class Suffix {

    private final String suffixId;

    private final String suffix;

    /**
     * Creates a new suffix object
     * @param suffixId The id of the suffix ("hello")
     * @param suffix The suffix of the rank ("<red>Hello")
     */
    public Suffix(@NotNull String suffixId, @NotNull String suffix) {
        this.suffixId = suffixId;
        this.suffix = suffix;
    }

    /**
     * @return The id of the suffix
     */
    public @NotNull String getSuffixId() {
        return suffixId;
    }


    /**
     * @return The suffix of the rank
     */
    public @NotNull String getSuffix() {
        return suffix;
    }


    /**
     * A utility method to convert a suffix object to a BSON document for MongoDB
     * @return The BSON document
     */
    public @NotNull Document toDocument() {
        return new Document("suffixId", suffixId)
                .append("suffix", suffix);
    }

    /**
     * A utility method to convert a BSON document to a suffix object
     * @param document The BSON document
     * @return The suffix object
     */
    public static @NotNull Suffix fromDocument(@NotNull Document document) {
        return new Suffix(document.getString("suffixId"), document.getString("suffix"));
    }

}
