package com.example.toolkit;

import java.util.regex.Pattern;

/**
 * Pure-function input validators. Every method returns {@code boolean}
 * and never throws on bad input (null → false).
 */
public final class Validators {

    private Validators() {}

    // ── Email ────────────────────────────────────────────────────────────────
    private static final Pattern EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s.strip()).matches();
    }

    // ── URL ──────────────────────────────────────────────────────────────────
    private static final Pattern URL = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);

    public static boolean isUrl(String s) {
        return s != null && URL.matcher(s.strip()).matches();
    }

    // ── Integer ──────────────────────────────────────────────────────────────
    public static boolean isInteger(String s) {
        if (StringUtils.isBlank(s)) return false;
        String t = s.strip();
        int start = (t.charAt(0) == '-' || t.charAt(0) == '+') ? 1 : 0;
        if (start == t.length()) return false;
        for (int i = start; i < t.length(); i++) {
            if (!Character.isDigit(t.charAt(i))) return false;
        }
        return true;
    }

    // ── Numeric (decimal) ────────────────────────────────────────────────────
    public static boolean isNumeric(String s) {
        if (StringUtils.isBlank(s)) return false;
        try {
            new java.math.BigDecimal(s.strip());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ── Phone (E.164 or local 10-digit) ─────────────────────────────────────
    private static final Pattern PHONE_E164 = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    private static final Pattern PHONE_LOCAL = Pattern.compile("^\\d{10}$");

    public static boolean isPhone(String s) {
        if (s == null) return false;
        String t = s.strip().replaceAll("[\\s\\-().]+", "");
        return PHONE_E164.matcher(t).matches() || PHONE_LOCAL.matcher(t).matches();
    }

    // ── Postal / ZIP code ────────────────────────────────────────────────────
    private static final Pattern ZIP_US    = Pattern.compile("^\\d{5}(-\\d{4})?$");
    private static final Pattern POSTAL_CA = Pattern.compile(
            "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$");
    private static final Pattern POSTAL_UK = Pattern.compile(
            "^[A-Za-z]{1,2}\\d[A-Za-z\\d]? \\d[A-Za-z]{2}$");

    public static boolean isZipUs(String s)     { return s != null && ZIP_US.matcher(s.strip()).matches(); }
    public static boolean isPostalCa(String s)  { return s != null && POSTAL_CA.matcher(s.strip()).matches(); }
    public static boolean isPostalUk(String s)  { return s != null && POSTAL_UK.matcher(s.strip()).matches(); }

    // ── Credit card (Luhn) ───────────────────────────────────────────────────
    public static boolean isCreditCard(String s) {
        if (StringUtils.isBlank(s)) return false;
        String digits = s.strip().replaceAll("[\\s\\-]", "");
        if (!digits.matches("\\d{13,19}")) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    // ── UUID ─────────────────────────────────────────────────────────────────
    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isUuid(String s) {
        return s != null && UUID.matcher(s.strip()).matches();
    }

    // ── IP address ───────────────────────────────────────────────────────────
    private static final Pattern IPV4 = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    public static boolean isIpv4(String s) {
        return s != null && IPV4.matcher(s.strip()).matches();
    }

    // ── Alphanumeric / identifier ────────────────────────────────────────────
    public static boolean isAlphanumeric(String s) {
        if (StringUtils.isBlank(s)) return false;
        return s.chars().allMatch(Character::isLetterOrDigit);
    }

    /** A valid identifier: starts with letter or '_', followed by word chars. */
    public static boolean isIdentifier(String s) {
        if (StringUtils.isBlank(s)) return false;
        char first = s.charAt(0);
        if (!Character.isLetter(first) && first != '_') return false;
        return s.chars().skip(1).allMatch(c -> Character.isLetterOrDigit(c) || c == '_');
    }

    // ── Range checks ─────────────────────────────────────────────────────────
    public static boolean inRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /** Returns true if {@code s} length is between minLen and maxLen (inclusive). */
    public static boolean hasLength(String s, int minLen, int maxLen) {
        if (s == null) return minLen == 0;
        return s.length() >= minLen && s.length() <= maxLen;
    }
}
