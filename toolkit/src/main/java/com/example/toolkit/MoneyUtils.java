package com.example.toolkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Pure-function money/rounding utilities.
 * Uses {@link BigDecimal} throughout; never {@code double} for amounts.
 */
public final class MoneyUtils {

    private MoneyUtils() {}

    /** Standard rounding mode used across the toolkit: HALF_UP. */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    /**
     * Rounds {@code amount} to {@code decimalPlaces} using {@link #DEFAULT_ROUNDING}.
     *
     * @throws IllegalArgumentException if decimalPlaces is negative
     */
    public static BigDecimal round(BigDecimal amount, int decimalPlaces) {
        Objects.requireNonNull(amount, "amount");
        if (decimalPlaces < 0) throw new IllegalArgumentException("decimalPlaces must be >= 0");
        return amount.setScale(decimalPlaces, DEFAULT_ROUNDING);
    }

    /**
     * Rounds to the nearest multiple of {@code unit}.
     * e.g. {@code roundToNearest(new BigDecimal("12.34"), new BigDecimal("0.05"))} → 12.35
     */
    public static BigDecimal roundToNearest(BigDecimal amount, BigDecimal unit) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(unit, "unit");
        if (unit.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("unit must be positive");
        return amount.divide(unit, 0, DEFAULT_ROUNDING).multiply(unit);
    }

    /**
     * Splits {@code total} into {@code parts} shares, distributing any
     * remainder penny by penny to the first shares so the sum is exact.
     *
     * @throws IllegalArgumentException if parts < 1
     */
    public static List<BigDecimal> split(BigDecimal total, int parts, int decimalPlaces) {
        Objects.requireNonNull(total, "total");
        if (parts < 1) throw new IllegalArgumentException("parts must be >= 1");
        // Work in smallest units (e.g. cents) to avoid scale confusion
        BigDecimal unit = BigDecimal.ONE.scaleByPowerOfTen(-decimalPlaces);
        long totalUnits = total.setScale(decimalPlaces, DEFAULT_ROUNDING)
                .movePointRight(decimalPlaces).setScale(0, DEFAULT_ROUNDING).longValueExact();
        long base = totalUnits / parts;
        long remainder = Math.abs(totalUnits % parts);
        boolean negative = totalUnits < 0;
        BigDecimal[] result = new BigDecimal[parts];
        for (int i = 0; i < parts; i++) {
            long share = Math.abs(base) + (i < remainder ? 1 : 0);
            result[i] = unit.multiply(BigDecimal.valueOf(negative ? -share : share));
        }
        return List.of(result);
    }

    /**
     * Applies a percentage discount. {@code pct} is expressed as a percentage (e.g. 10 = 10%).
     */
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal pct, int decimalPlaces) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(pct, "pct");
        BigDecimal factor = BigDecimal.ONE.subtract(
                pct.divide(BigDecimal.valueOf(100), 10, DEFAULT_ROUNDING));
        return round(amount.multiply(factor), decimalPlaces);
    }

    /**
     * Applies a tax rate to produce a gross (inclusive) amount.
     * {@code taxRate} is expressed as a percentage (e.g. 20 = 20%).
     */
    public static BigDecimal addTax(BigDecimal net, BigDecimal taxRate, int decimalPlaces) {
        Objects.requireNonNull(net, "net");
        Objects.requireNonNull(taxRate, "taxRate");
        BigDecimal factor = BigDecimal.ONE.add(
                taxRate.divide(BigDecimal.valueOf(100), 10, DEFAULT_ROUNDING));
        return round(net.multiply(factor), decimalPlaces);
    }

    /**
     * Extracts the net amount from a tax-inclusive gross.
     */
    public static BigDecimal removeVat(BigDecimal gross, BigDecimal taxRate, int decimalPlaces) {
        Objects.requireNonNull(gross, "gross");
        Objects.requireNonNull(taxRate, "taxRate");
        BigDecimal divisor = BigDecimal.ONE.add(
                taxRate.divide(BigDecimal.valueOf(100), 10, DEFAULT_ROUNDING));
        return round(gross.divide(divisor, 10, DEFAULT_ROUNDING), decimalPlaces);
    }

    /**
     * Calculates simple interest: {@code principal * rate * time}.
     * {@code rate} is a fraction per period (e.g. 0.05 for 5%).
     */
    public static BigDecimal simpleInterest(BigDecimal principal, BigDecimal rate,
                                             BigDecimal periods, int decimalPlaces) {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(periods, "periods");
        return round(principal.multiply(rate).multiply(periods), decimalPlaces);
    }

    /**
     * Calculates compound interest final amount:
     * {@code principal * (1 + rate)^n}.
     */
    public static BigDecimal compoundAmount(BigDecimal principal, BigDecimal rate,
                                             int periods, int decimalPlaces) {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(rate, "rate");
        BigDecimal factor = BigDecimal.ONE.add(rate);
        BigDecimal result = principal;
        for (int i = 0; i < periods; i++) {
            result = result.multiply(factor);
        }
        return round(result, decimalPlaces);
    }

    /**
     * Clamps {@code amount} to [{@code min}, {@code max}].
     */
    public static BigDecimal clamp(BigDecimal amount, BigDecimal min, BigDecimal max) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(min, "min");
        Objects.requireNonNull(max, "max");
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("min > max");
        if (amount.compareTo(min) < 0) return min;
        if (amount.compareTo(max) > 0) return max;
        return amount;
    }
}
