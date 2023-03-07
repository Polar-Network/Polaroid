package net.polar.utils;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class StringUtils {

    private StringUtils(){}

    private static final char[] MC_USERNAME_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray();

    public static @NotNull String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(MC_USERNAME_CHARACTERS[(int) (Math.random() * MC_USERNAME_CHARACTERS.length)]);
        }
        return sb.toString();
    }

    public static @NotNull String generateNPCName() {
        return generateRandomString(10);
    }

}
