package com.example.toolkit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorsTest {

    // ── isEmail ───────────────────────────────────────────────────────────────

    @Test
    void isEmail_valid() {
        assertTrue(Validators.isEmail("user@example.com"));
        assertTrue(Validators.isEmail("a.b+tag@sub.domain.org"));
    }

    @Test
    void isEmail_invalid() {
        assertFalse(Validators.isEmail(null));
        assertFalse(Validators.isEmail(""));
        assertFalse(Validators.isEmail("notanemail"));
        assertFalse(Validators.isEmail("missing@dot"));
        assertFalse(Validators.isEmail("@nodomain.com"));
    }

    // ── isUrl ─────────────────────────────────────────────────────────────────

    @Test
    void isUrl_valid() {
        assertTrue(Validators.isUrl("https://example.com"));
        assertTrue(Validators.isUrl("http://foo.bar/path?q=1"));
    }

    @Test
    void isUrl_invalid() {
        assertFalse(Validators.isUrl(null));
        assertFalse(Validators.isUrl("ftp-nope://broken"));
        assertFalse(Validators.isUrl("just text"));
    }

    // ── isInteger ─────────────────────────────────────────────────────────────

    @Test
    void isInteger_positiveAndNegative() {
        assertTrue(Validators.isInteger("42"));
        assertTrue(Validators.isInteger("-7"));
        assertTrue(Validators.isInteger("+100"));
    }

    @Test
    void isInteger_decimal_false() {
        assertFalse(Validators.isInteger("3.14"));
        assertFalse(Validators.isInteger(""));
        assertFalse(Validators.isInteger(null));
    }

    // ── isNumeric ─────────────────────────────────────────────────────────────

    @Test
    void isNumeric_various() {
        assertTrue(Validators.isNumeric("3.14"));
        assertTrue(Validators.isNumeric("-0.001"));
        assertTrue(Validators.isNumeric("1e10"));
        assertFalse(Validators.isNumeric("abc"));
        assertFalse(Validators.isNumeric(null));
    }

    // ── isPhone ───────────────────────────────────────────────────────────────

    @Test
    void isPhone_e164() {
        assertTrue(Validators.isPhone("+14155552671"));
        assertTrue(Validators.isPhone("+442071838750"));
    }

    @Test
    void isPhone_localTenDigits() {
        assertTrue(Validators.isPhone("4155552671"));
    }

    @Test
    void isPhone_invalid() {
        assertFalse(Validators.isPhone("123"));
        assertFalse(Validators.isPhone(null));
    }

    // ── isZipUs ──────────────────────────────────────────────────────────────

    @Test
    void isZipUs_valid() {
        assertTrue(Validators.isZipUs("90210"));
        assertTrue(Validators.isZipUs("90210-1234"));
    }

    @Test
    void isZipUs_invalid() {
        assertFalse(Validators.isZipUs("9021"));
        assertFalse(Validators.isZipUs("ABCDE"));
    }

    // ── isCreditCard (Luhn) ──────────────────────────────────────────────────

    @Test
    void isCreditCard_validLuhn() {
        // Standard Luhn test numbers
        assertTrue(Validators.isCreditCard("4532015112830366")); // Visa
        assertTrue(Validators.isCreditCard("5425233430109903")); // MC
    }

    @Test
    void isCreditCard_invalidLuhn() {
        assertFalse(Validators.isCreditCard("1234567890123456"));
        assertFalse(Validators.isCreditCard(null));
        assertFalse(Validators.isCreditCard(""));
    }

    @Test
    void isCreditCard_withSpacesAndDashes() {
        assertTrue(Validators.isCreditCard("4532 0151 1283 0366"));
        assertTrue(Validators.isCreditCard("4532-0151-1283-0366"));
    }

    // ── isUuid ───────────────────────────────────────────────────────────────

    @Test
    void isUuid_valid() {
        assertTrue(Validators.isUuid("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void isUuid_invalid() {
        assertFalse(Validators.isUuid("not-a-uuid"));
        assertFalse(Validators.isUuid(null));
        assertFalse(Validators.isUuid("550e8400-e29b-41d4-a716-44665544000Z"));
    }

    // ── isIpv4 ───────────────────────────────────────────────────────────────

    @Test
    void isIpv4_valid() {
        assertTrue(Validators.isIpv4("192.168.1.1"));
        assertTrue(Validators.isIpv4("0.0.0.0"));
        assertTrue(Validators.isIpv4("255.255.255.255"));
    }

    @Test
    void isIpv4_outOfRange() {
        assertFalse(Validators.isIpv4("256.0.0.1"));
        assertFalse(Validators.isIpv4("192.168.1"));
    }

    // ── isAlphanumeric / isIdentifier ─────────────────────────────────────────

    @Test
    void isAlphanumeric_valid() {
        assertTrue(Validators.isAlphanumeric("abc123"));
        assertFalse(Validators.isAlphanumeric("abc 123"));
        assertFalse(Validators.isAlphanumeric(""));
    }

    @Test
    void isIdentifier_valid() {
        assertTrue(Validators.isIdentifier("_myVar"));
        assertTrue(Validators.isIdentifier("count2"));
        assertFalse(Validators.isIdentifier("2count"));
        assertFalse(Validators.isIdentifier("my-var"));
    }

    // ── range checks ─────────────────────────────────────────────────────────

    @Test
    void inRange_long() {
        assertTrue(Validators.inRange(5L, 1L, 10L));
        assertFalse(Validators.inRange(0L, 1L, 10L));
    }

    @Test
    void hasLength_boundsCheck() {
        assertTrue(Validators.hasLength("hello", 3, 10));
        assertFalse(Validators.hasLength("hi", 3, 10));
        assertFalse(Validators.hasLength("toolongstring", 1, 5));
    }
}
