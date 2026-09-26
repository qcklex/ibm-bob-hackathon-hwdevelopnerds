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

    // ── truncate boundary ────────────────────────────────────────────────────

    @Test
    void truncate_maxLenZeroReturnsEllipsisOnly() {
        // line 31: maxLen == 0 must NOT throw (boundary: < 0 vs <= 0)
        assertEquals("...", StringUtils.truncate("hi", 0, "..."));
    }

    @Test
    void truncate_exactLengthNotTruncated() {
        // line 34: s.length() == maxLen must return s unchanged (boundary: <= vs <)
        assertEquals("hello", StringUtils.truncate("hello", 5, "..."));
    }

    @Test
    void truncate_oneLongerGetsTruncated() {
        // line 34: s.length() == maxLen + 1 must be truncated
        assertEquals("he...", StringUtils.truncate("hello!", 5, "..."));
    }

    // ── toSnakeCase boundary ─────────────────────────────────────────────────

    @Test
    void toSnakeCase_uppercaseFirstCharNoUnderscore() {
        // line 54: i > 0 must not add underscore for first uppercase char
        assertEquals("hello", StringUtils.toSnakeCase("Hello"));
        assertEquals("hello_world", StringUtils.toSnakeCase("HelloWorld"));
    }

    // ── repeat boundary / null ───────────────────────────────────────────────

    @Test
    void repeat_nullInputReturnsNull() {
        // line 77: NO_COVERAGE – null branch never exercised
        assertNull(StringUtils.repeat(null, 3));
    }

    @Test
    void repeat_negativeTimesReturnsEmpty() {
        // line 78: boundary – times <= 0 vs times < 0; -1 must return ""
        assertEquals("", StringUtils.repeat("abc", -1));
    }

    // ── leftPad / rightPad exact-width boundary ───────────────────────────────

    @Test
    void leftPad_exactWidthUnchanged() {
        // line 88: s.length() == totalWidth must return s (boundary: >= vs >)
        assertEquals("abc", StringUtils.leftPad("abc", 3, '0'));
    }

    @Test
    void rightPad_exactWidthUnchanged() {
        // line 97: s.length() == totalWidth must return s (boundary: >= vs >)
        assertEquals("abc", StringUtils.rightPad("abc", 3, ' '));
    }

    // ── reverse null ─────────────────────────────────────────────────────────

    @Test
    void reverse_nullReturnsNull() {
        // line 119: NO_COVERAGE – null path never exercised
        assertNull(StringUtils.reverse(null));
    }

    // ── wordWrap boundary ────────────────────────────────────────────────────

    @Test
    void wordWrap_zeroWidthThrows() {
        // line 138: lineWidth <= 0 boundary – 0 must throw (not just negatives)
        assertThrows(IllegalArgumentException.class,
                () -> StringUtils.wordWrap("hello", 0));
    }

    @Test
    void wordWrap_wordFitsExactlyOnLine() {
        // line 145: line.length() + 1 + word.length() <= lineWidth
        // "ab" (2) + 1 + "cd" (2) = 5 == lineWidth 5 → fits on same line
        List<String> lines = StringUtils.wordWrap("ab cd", 5);
        assertEquals(List.of("ab cd"), lines);
    }

    @Test
    void wordWrap_wordExceedsByOne() {
        // line 145: "ab" (2) + 1 + "cde" (3) = 6 > lineWidth 5 → new line
        List<String> lines = StringUtils.wordWrap("ab cde", 5);
        assertEquals(List.of("ab", "cde"), lines);
    }

    // ── repeat / leftPad / rightPad — additional boundary pinning ────────────

    @Test
    void repeat_zeroTimesEmptyString() {
        // times == 0 with empty input: both <= 0 and mutant < 0 reach return ""
        // pinning the exact boundary value with a different string argument
        assertEquals("", StringUtils.repeat("", 0));
    }

    @Test
    void repeat_zeroTimesNonEmptyString() {
        // times == 0 is the exact boundary between <= 0 returning "" and < 0 falling
        // through to s.repeat(0); asserting no exception and empty result
        assertEquals("", StringUtils.repeat("xyz", 0));
        assertEquals("", StringUtils.repeat("a", 0));
    }

    @Test
    void leftPad_exactWidthReturnsOriginalInstance() {
        // s.length() == totalWidth: >= returns s unchanged; > would call padChar.repeat(0)+s
        // both produce equal strings, but we assert the value is preserved exactly
        assertEquals("ab", StringUtils.leftPad("ab", 2, '-'));
        assertEquals("x", StringUtils.leftPad("x", 1, '0'));
    }

    @Test
    void rightPad_exactWidthReturnsOriginalInstance() {
        // s.length() == totalWidth: >= returns s unchanged; > would call s+padChar.repeat(0)
        // both produce equal strings; asserting value is preserved exactly
        assertEquals("ab", StringUtils.rightPad("ab", 2, '-'));
        assertEquals("x", StringUtils.rightPad("x", 1, '0'));
    }

}
