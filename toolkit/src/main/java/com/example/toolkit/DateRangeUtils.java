package com.example.toolkit;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pure-function utilities for working with date ranges represented as
 * [start, end] pairs of {@link LocalDate} (both ends inclusive).
 */
public final class DateRangeUtils {

    private DateRangeUtils() {}

    /**
     * Returns the number of days in the range [start, end], inclusive.
     *
     * @throws IllegalArgumentException if start is after end
     */
    public static long lengthInDays(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (start.isAfter(end)) throw new IllegalArgumentException("start must not be after end");
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * Returns {@code true} if the two ranges overlap (inclusive on both ends).
     */
    public static boolean overlaps(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        Objects.requireNonNull(s1, "s1");
        Objects.requireNonNull(e1, "e1");
        Objects.requireNonNull(s2, "s2");
        Objects.requireNonNull(e2, "e2");
        return !s1.isAfter(e2) && !s2.isAfter(e1);
    }

    /**
     * Returns the intersection of the two ranges, or {@code null} if they do not overlap.
     * Result is a two-element array [intersectStart, intersectEnd].
     */
    public static LocalDate[] intersection(LocalDate s1, LocalDate e1,
                                            LocalDate s2, LocalDate e2) {
        if (!overlaps(s1, e1, s2, e2)) return null;
        return new LocalDate[]{
            s1.isAfter(s2) ? s1 : s2,
            e1.isBefore(e2) ? e1 : e2
        };
    }

    /**
     * Checks whether {@code date} falls within [start, end] (inclusive).
     */
    public static boolean contains(LocalDate start, LocalDate end, LocalDate date) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        Objects.requireNonNull(date, "date");
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Expands a range by {@code days} on each side.
     * Returns a two-element array [newStart, newEnd].
     */
    public static LocalDate[] expand(LocalDate start, LocalDate end, long days) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (days < 0) throw new IllegalArgumentException("days must be >= 0");
        return new LocalDate[]{ start.minusDays(days), end.plusDays(days) };
    }

    /**
     * Splits [start, end] into chunks of at most {@code chunkDays} days each.
     * Each inner array is a [chunkStart, chunkEnd] pair.
     */
    public static List<LocalDate[]> splitIntoChunks(LocalDate start, LocalDate end, long chunkDays) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (chunkDays <= 0) throw new IllegalArgumentException("chunkDays must be > 0");
        if (start.isAfter(end)) throw new IllegalArgumentException("start must not be after end");
        List<LocalDate[]> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            LocalDate chunkEnd = cursor.plusDays(chunkDays - 1);
            if (chunkEnd.isAfter(end)) chunkEnd = end;
            result.add(new LocalDate[]{ cursor, chunkEnd });
            cursor = chunkEnd.plusDays(1);
        }
        return result;
    }

    /**
     * Returns a list of all dates in [start, end] that are weekdays (Mon-Fri).
     */
    public static List<LocalDate> weekdaysInRange(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (start.isAfter(end)) return List.of();
        List<LocalDate> result = new ArrayList<>();
        LocalDate d = start;
        while (!d.isAfter(end)) {
            switch (d.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> {} // skip
                default -> result.add(d);
            }
            d = d.plusDays(1);
        }
        return List.copyOf(result);
    }

    /**
     * Merges two overlapping or adjacent ranges into one.
     * Returns null if they are neither overlapping nor adjacent.
     * Result is a two-element array [mergedStart, mergedEnd].
     */
    public static LocalDate[] merge(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        Objects.requireNonNull(s1, "s1");
        Objects.requireNonNull(e1, "e1");
        Objects.requireNonNull(s2, "s2");
        Objects.requireNonNull(e2, "e2");
        // adjacent means e1+1 == s2 or e2+1 == s1
        boolean adjacent = e1.plusDays(1).equals(s2) || e2.plusDays(1).equals(s1);
        if (!overlaps(s1, e1, s2, e2) && !adjacent) return null;
        LocalDate mergedStart = s1.isBefore(s2) ? s1 : s2;
        LocalDate mergedEnd   = e1.isAfter(e2)  ? e1 : e2;
        return new LocalDate[]{ mergedStart, mergedEnd };
    }

    /**
     * Returns the number of complete weeks in the range.
     */
    public static long completeWeeks(LocalDate start, LocalDate end) {
        long days = lengthInDays(start, end);
        return days / 7;
    }

    /**
     * Shifts a range by {@code days} forward (positive) or backward (negative).
     */
    public static LocalDate[] shift(LocalDate start, LocalDate end, long days) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        return new LocalDate[]{ start.plusDays(days), end.plusDays(days) };
    }
}
