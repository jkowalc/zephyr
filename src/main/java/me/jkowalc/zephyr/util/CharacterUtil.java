package me.jkowalc.zephyr.util;

public class CharacterUtil {
    public static String getRepresentation(char c) {
        return switch (c) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            case '\'' -> "\\'";
            case '\"' -> "\\\"";
            case '\\' -> "\\\\";
            default -> String.valueOf(c);
        };
    }
    public static String getRepresentation(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(getRepresentation(c));
        }
        return sb.toString();
    }
}
