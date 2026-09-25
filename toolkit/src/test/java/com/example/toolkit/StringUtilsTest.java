package com.example.toolkit;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    // ── isEmpty / isBlank ────────────────────────────────────────────────────

    @Test
    void isEmpty_nullAndEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("a"));
    }

    @Test
    void isBlank_whitespaceIsBlank() {
        assertTrue(StringUtils.isBlank("   "));
        assertTrue(StringUtils.isBlank("\t\n"));
        assertFalse(StringUtils.isBlank("x"));
    }

    // ── truncate ─────────────────────────────────────────────────────────────

    @Test
    void truncate_shortStringUnchanged() {
        assertEquals("hello", StringUtils.truncate("hello", 10, "..."));
    }

    @Test
    void truncate_longStringGetsEllipsis() {
        assertEquals("he...", StringUtils.truncate("hello world", 5, "..."));
    }

    @Test
    void truncate_nullInputReturnsNull() {
        assertNull(StringUtils.truncate(null, 5, "..."));
    }

    @Test
    void truncate_negativeLengthThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> StringUtils.truncate("abc", -1, "..."));
    }

    // ── capitalise ───────────────────────────────────────────────────────────

    @Test
    void capitalise_lowercaseFirst() {
        assertEquals("Hello", StringUtils.capitalise("hello"));
    }

    @Test
    void capitalise_alreadyCapital() {
        assertEquals("Hello", StringUtils.capitalise("Hello"));
    }

    @Test
    void capitalise_nullAndEmptyPassThrough() {
        assertNull(StringUtils.capitalise(null));
        assertEquals("", StringUtils.capitalise(""));
    }

    // ── toSnakeCase ──────────────────────────────────────────────────────────

    @Test
    void toSnakeCase_camelCase() {
        assertEquals("get_user_name", StringUtils.toSnakeCase("getUserName"));
    }

    @Test
    void toSnakeCase_alreadyLower() {
        assertEquals("hello", StringUtils.toSnakeCase("hello"));
    }

    @Test
    void toSnakeCase_nullPassThrough() {
        assertNull(StringUtils.toSnakeCase(null));
    }

    // ── splitAndTrim ─────────────────────────────────────────────────────────

    @Test
    void splitAndTrim_typicalCsvLine() {
        List<String> parts = StringUtils.splitAndTrim("  a , b ,, c  ", ",");
        assertEquals(List.of("a", "b", "c"), parts);
    }

    @Test
    void splitAndTrim_emptyInputReturnsEmptyList() {
        assertEquals(List.of(), StringUtils.splitAndTrim("", ","));
        assertEquals(List.of(), StringUtils.splitAndTrim(null, ","));
    }

    // ── repeat ───────────────────────────────────────────────────────────────

    @Test
    void repeat_basic() {
        assertEquals("abcabcabc", StringUtils.repeat("abc", 3));
    }

    @Test
    void repeat_zeroTimesReturnsEmpty() {
        assertEquals("", StringUtils.repeat("abc", 0));
    }

    // ── leftPad / rightPad ───────────────────────────────────────────────────

    @Test
    void leftPad_shorterString() {
        assertEquals("007", StringUtils.leftPad("7", 3, '0'));
    }

    @Test
    void leftPad_alreadyWide() {
        assertEquals("hello", StringUtils.leftPad("hello", 3, ' '));
    }

    @Test
    void rightPad_basic() {
        assertEquals("hi   ", StringUtils.rightPad("hi", 5, ' '));
    }

    // ── countOccurrences ─────────────────────────────────────────────────────

    @Test
    void countOccurrences_multipleMatches() {
        assertEquals(3, StringUtils.countOccurrences("abcabcabc", "abc"));
    }

    @Test
    void countOccurrences_noMatch() {
        assertEquals(0, StringUtils.countOccurrences("hello", "xyz"));
    }

    @Test
    void countOccurrences_emptySubReturnZero() {
        assertEquals(0, StringUtils.countOccurrences("hello", ""));
    }

    // ── reverse ──────────────────────────────────────────────────────────────

    @Test
    void reverse_basic() {
        assertEquals("olleh", StringUtils.reverse("hello"));
    }

    @Test
    void reverse_singleChar() {
        assertEquals("a", StringUtils.reverse("a"));
    }

    // ── isPalindrome ─────────────────────────────────────────────────────────

    @Test
    void isPalindrome_true() {
        assertTrue(StringUtils.isPalindrome("racecar"));
        assertTrue(StringUtils.isPalindrome("A man a plan a canal panama".replace(" ", "")));
    }

    @Test
    void isPalindrome_false() {
        assertFalse(StringUtils.isPalindrome("hello"));
    }

    @Test
    void isPalindrome_nullReturnsFalse() {
        assertFalse(StringUtils.isPalindrome(null));
    }

    // ── wordWrap ─────────────────────────────────────────────────────────────

    @Test
    void wordWrap_typicalSentence() {
        List<String> lines = StringUtils.wordWrap("The quick brown fox jumps", 15);
        assertEquals(List.of("The quick brown", "fox jumps"), lines);
    }

    @Test
    void wordWrap_emptyStringReturnsEmptyList() {
        assertEquals(List.of(), StringUtils.wordWrap("", 10));
    }

    @Test
    void wordWrap_singleWordShorterThanWidth() {
        assertEquals(List.of("hello"), StringUtils.wordWrap("hello", 20));
    }
}
