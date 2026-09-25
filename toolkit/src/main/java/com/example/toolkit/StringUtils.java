package com.example.toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pure-function string utilities. All methods are null-safe and stateless.
 */
public final class StringUtils {

    private StringUtils() {}

    /** Returns true if {@code s} is null or empty. */
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /** Returns true if {@code s} is null, empty, or contains only whitespace. */
    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Truncates {@code s} to at most {@code maxLen} characters.
     * Appends {@code ellipsis} when truncation occurs.
     *
     * @throws IllegalArgumentException if maxLen is negative
     */
    public static String truncate(String s, int maxLen, String ellipsis) {
        if (maxLen < 0) throw new IllegalArgumentException("maxLen must be >= 0, got " + maxLen);
        if (s == null) return null;
        String e = ellipsis == null ? "" : ellipsis;
        if (s.length() <= maxLen) return s;
        int cutAt = Math.max(0, maxLen - e.length());
        return s.substring(0, cutAt) + e;
    }

    /** Capitalises the first character; leaves the rest unchanged. */
    public static String capitalise(String s) {
        if (isEmpty(s)) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Converts camelCase or PascalCase to snake_case.
     * e.g. {@code "getUserName"} → {@code "get_user_name"}
     */
    public static String toSnakeCase(String s) {
        if (isEmpty(s)) return s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * Splits {@code s} on {@code delimiter}, trims each part, and drops blanks.
     */
    public static List<String> splitAndTrim(String s, String delimiter) {
        if (isEmpty(s)) return List.of();
        List<String> result = new ArrayList<>();
        for (String part : s.split(java.util.regex.Pattern.quote(delimiter), -1)) {
            String trimmed = part.strip();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return List.copyOf(result);
    }

    /** Repeats {@code s} exactly {@code times} times. */
    public static String repeat(String s, int times) {
        if (s == null) return null;
        if (times <= 0) return "";
        return s.repeat(times);
    }

    /**
     * Left-pads {@code s} with {@code padChar} to reach {@code totalWidth}.
     * Returns {@code s} unchanged if already at or beyond {@code totalWidth}.
     */
    public static String leftPad(String s, int totalWidth, char padChar) {
        if (s == null) s = "";
        if (s.length() >= totalWidth) return s;
        return String.valueOf(padChar).repeat(totalWidth - s.length()) + s;
    }

    /**
     * Right-pads {@code s} with {@code padChar} to reach {@code totalWidth}.
     */
    public static String rightPad(String s, int totalWidth, char padChar) {
        if (s == null) s = "";
        if (s.length() >= totalWidth) return s;
        return s + String.valueOf(padChar).repeat(totalWidth - s.length());
    }

    /**
     * Counts non-overlapping occurrences of {@code sub} within {@code s}.
     * Returns 0 if either argument is null or empty.
     */
    public static int countOccurrences(String s, String sub) {
        if (isEmpty(s) || isEmpty(sub)) return 0;
        int count = 0, idx = 0;
        while ((idx = s.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * Reverses the characters in {@code s}.
     */
    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }

    /**
     * Returns {@code true} if {@code s} is a palindrome (case-insensitive, ignoring spaces).
     */
    public static boolean isPalindrome(String s) {
        if (s == null) return false;
        String cleaned = s.toLowerCase().replaceAll("\\s+", "");
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    /**
     * Wraps text to lines of at most {@code lineWidth} characters,
     * breaking only on whitespace boundaries.
     */
    public static List<String> wordWrap(String s, int lineWidth) {
        if (isEmpty(s)) return List.of();
        if (lineWidth <= 0) throw new IllegalArgumentException("lineWidth must be > 0");
        List<String> lines = new ArrayList<>();
        String[] words = s.strip().split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() == 0) {
                line.append(word);
            } else if (line.length() + 1 + word.length() <= lineWidth) {
                line.append(' ').append(word);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return List.copyOf(lines);
    }
}
