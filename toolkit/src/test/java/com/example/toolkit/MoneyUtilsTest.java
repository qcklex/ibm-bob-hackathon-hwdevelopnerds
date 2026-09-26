package com.example.toolkit;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoneyUtilsTest {

    // ── round ────────────────────────────────────────────────────────────────

    @Test
    void round_halfUp() {
        // 1.225 → HALF_UP gives 1.23; HALF_EVEN would give 1.22 (rounds to even)
        // This uniquely distinguishes HALF_UP from HALF_EVEN.
        assertEquals(new BigDecimal("1.23"), MoneyUtils.round(new BigDecimal("1.225"), 2));
        // 1.224 → HALF_UP gives 1.22 (below the .5 threshold, rounds down)
        assertEquals(new BigDecimal("1.22"), MoneyUtils.round(new BigDecimal("1.224"), 2));
    }

    @Test
    void round_noRoundingNeeded() {
        assertEquals(new BigDecimal("1.20"), MoneyUtils.round(new BigDecimal("1.2"), 2));
    }

    @Test
    void round_negativeDecimalPlacesThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> MoneyUtils.round(new BigDecimal("1.5"), -1));
    }

    @Test
    void round_zeroDecimalPlacesIsAllowed() {
        // Kills the < 0 → <= 0 boundary mutant: decimalPlaces=0 must NOT throw
        assertEquals(0, new BigDecimal("2").compareTo(
                MoneyUtils.round(new BigDecimal("1.7"), 0)));
    }

    // ── roundToNearest ───────────────────────────────────────────────────────

    @Test
    void roundToNearest_fiveCents() {
        BigDecimal result = MoneyUtils.roundToNearest(
                new BigDecimal("12.33"), new BigDecimal("0.05"));
        assertEquals(0, new BigDecimal("12.35").compareTo(result));
    }

    @Test
    void roundToNearest_nearestTen() {
        BigDecimal result = MoneyUtils.roundToNearest(
                new BigDecimal("46"), new BigDecimal("10"));
        assertEquals(0, new BigDecimal("50").compareTo(result));
    }

    @Test
    void roundToNearest_zeroUnitThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> MoneyUtils.roundToNearest(BigDecimal.ONE, BigDecimal.ZERO));
    }

    // ── split ────────────────────────────────────────────────────────────────

    @Test
    void split_evenAmount() {
        List<BigDecimal> parts = MoneyUtils.split(new BigDecimal("30.00"), 3, 2);
        assertEquals(3, parts.size());
        BigDecimal sum = parts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("30.00").compareTo(sum));
        parts.forEach(p -> assertEquals(0, new BigDecimal("10.00").compareTo(p)));
    }

    @Test
    void split_unevenAmount_sumIsExact() {
        List<BigDecimal> parts = MoneyUtils.split(new BigDecimal("10.00"), 3, 2);
        BigDecimal sum = parts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("10.00").compareTo(sum));
        // first share gets the extra penny
        assertEquals(0, new BigDecimal("3.34").compareTo(parts.get(0)));
        assertEquals(0, new BigDecimal("3.33").compareTo(parts.get(1)));
    }

    @Test
    void split_singlePart() {
        List<BigDecimal> parts = MoneyUtils.split(new BigDecimal("99.99"), 1, 2);
        assertEquals(1, parts.size());
        assertEquals(0, new BigDecimal("99.99").compareTo(parts.get(0)));
    }

    @Test
    void split_onePartIsAllowed() {
        // Kills the parts < 1 → parts <= 1 boundary mutant: parts=1 must NOT throw
        List<BigDecimal> result = MoneyUtils.split(new BigDecimal("5.00"), 1, 2);
        assertEquals(1, result.size());
        assertEquals(0, new BigDecimal("5.00").compareTo(result.get(0)));
    }

    @Test
    void split_negativeTotalPreservesSum() {
        // Kills the removed-negation NO_COVERAGE mutant: negative total must produce negative parts
        List<BigDecimal> parts = MoneyUtils.split(new BigDecimal("-9.00"), 3, 2);
        assertEquals(3, parts.size());
        parts.forEach(p -> assertEquals(0, new BigDecimal("-3.00").compareTo(p)));
        BigDecimal sum = parts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("-9.00").compareTo(sum));
    }

    // ── applyDiscount ────────────────────────────────────────────────────────

    @Test
    void applyDiscount_tenPercent() {
        BigDecimal result = MoneyUtils.applyDiscount(
                new BigDecimal("100.00"), new BigDecimal("10"), 2);
        assertEquals(0, new BigDecimal("90.00").compareTo(result));
    }

    @Test
    void applyDiscount_zeroPercent() {
        BigDecimal result = MoneyUtils.applyDiscount(
                new BigDecimal("50.00"), BigDecimal.ZERO, 2);
        assertEquals(0, new BigDecimal("50.00").compareTo(result));
    }

    // ── addTax / removeVat ───────────────────────────────────────────────────

    @Test
    void addTax_twentyPercent() {
        BigDecimal result = MoneyUtils.addTax(
                new BigDecimal("100.00"), new BigDecimal("20"), 2);
        assertEquals(0, new BigDecimal("120.00").compareTo(result));
    }

    @Test
    void removeVat_reverseOfAddTax() {
        BigDecimal net = new BigDecimal("100.00");
        BigDecimal gross = MoneyUtils.addTax(net, new BigDecimal("20"), 2);
        BigDecimal recovered = MoneyUtils.removeVat(gross, new BigDecimal("20"), 2);
        assertEquals(0, net.compareTo(recovered));
    }

    // ── simpleInterest ───────────────────────────────────────────────────────

    @Test
    void simpleInterest_standard() {
        BigDecimal result = MoneyUtils.simpleInterest(
                new BigDecimal("1000"), new BigDecimal("0.05"),
                new BigDecimal("3"), 2);
        assertEquals(0, new BigDecimal("150.00").compareTo(result));
    }

    // ── compoundAmount ───────────────────────────────────────────────────────

    @Test
    void compoundAmount_singlePeriod() {
        BigDecimal result = MoneyUtils.compoundAmount(
                new BigDecimal("100"), new BigDecimal("0.10"), 1, 2);
        assertEquals(0, new BigDecimal("110.00").compareTo(result));
    }

    @Test
    void compoundAmount_zeroPeriods() {
        BigDecimal result = MoneyUtils.compoundAmount(
                new BigDecimal("500"), new BigDecimal("0.05"), 0, 2);
        assertEquals(0, new BigDecimal("500.00").compareTo(result));
    }

    // ── clamp ────────────────────────────────────────────────────────────────

    @Test
    void clamp_withinRange() {
        BigDecimal result = MoneyUtils.clamp(
                new BigDecimal("50"), new BigDecimal("0"), new BigDecimal("100"));
        assertEquals(0, new BigDecimal("50").compareTo(result));
    }

    @Test
    void clamp_belowMin() {
        BigDecimal result = MoneyUtils.clamp(
                new BigDecimal("-5"), BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void clamp_aboveMax() {
        BigDecimal result = MoneyUtils.clamp(
                new BigDecimal("200"), BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, new BigDecimal("100").compareTo(result));
    }

    @Test
    void clamp_exactlyAtMin_returnsMin() {
        // Kills compareTo(min) < 0 → <= 0 boundary mutant: value == min must return min (not be treated as below min)
        BigDecimal result = MoneyUtils.clamp(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void clamp_exactlyAtMax_returnsMax() {
        // Kills compareTo(max) > 0 → >= 0 boundary mutant: value == max must return max (not be treated as above max)
        BigDecimal result = MoneyUtils.clamp(new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, new BigDecimal("100").compareTo(result));
    }

    @Test
    void clamp_justBelowMin_returnsMin() {
        // Confirms value just below min is clamped to min
        BigDecimal result = MoneyUtils.clamp(new BigDecimal("-0.01"), BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void clamp_justAboveMax_returnsMax() {
        // Confirms value just above max is clamped to max
        BigDecimal result = MoneyUtils.clamp(new BigDecimal("100.01"), BigDecimal.ZERO, new BigDecimal("100"));
        assertEquals(0, new BigDecimal("100").compareTo(result));
    }
}
