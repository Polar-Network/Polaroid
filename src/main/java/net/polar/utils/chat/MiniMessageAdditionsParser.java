package net.polar.utils.chat;

import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.polar.Polaroid;
import net.polar.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * A class for parsing MiniMessage additions
 */
public class MiniMessageAdditionsParser {
    private MiniMessageAdditionsParser() {}

    /**
     * Terribly slow method
     * @return The {@link TagResolver} with the additions
     */
    public static @NotNull TagResolver getAdditionsFromFile() {
        final File file = new File(Polaroid.getLocalPath().toFile(), "minimessage-additions.json");
        if (!file.exists()) {
            MiniMessageTag polarBlue = new MiniMessageTag("polar-blue", "<#001eff>");
            MiniMessageTag polarPink = new MiniMessageTag("polar-pink", "<#ff00a2>");
            MiniMessageTag polarCyan = new MiniMessageTag("polar-cyan", "<#00ffe1>");
            JsonUtils.prettyWrite(
                    List.of(polarBlue, polarPink, polarCyan),
                    TypeToken.getParameterized(List.class, MiniMessageTag.class).getType(),
                    file
            );
            return TagResolver.builder()
                    .resolver(Placeholder.parsed(polarBlue.name, polarBlue.replacement))
                    .resolver(Placeholder.parsed(polarPink.name, polarPink.replacement))
                    .resolver(Placeholder.parsed(polarCyan.name, polarCyan.replacement))
                    .resolvers(TagResolver.standard())
                    .build();
        }

        final List<MiniMessageTag> tags = JsonUtils.read(
                file,
                TypeToken.getParameterized(List.class, MiniMessageTag.class).getType()
        );

        TagResolver.@NotNull Builder resolver = TagResolver.builder();
        for (MiniMessageTag tag : tags) {
                resolver = resolver.resolver(Placeholder.parsed(tag.name, tag.replacement));
        }
        resolver = resolver.resolvers(TagResolver.standard());
        return resolver.build();
    }

    private static class MiniMessageTag {
        private final String name;
        private final String replacement;

        public MiniMessageTag(String name, String replacement) {
            this.name = name;
            this.replacement = replacement;
        }
    }
}
