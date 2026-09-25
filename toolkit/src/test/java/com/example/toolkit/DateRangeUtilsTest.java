package com.example.toolkit;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeUtilsTest {

    private static final LocalDate JAN1  = LocalDate.of(2024, 1, 1);
    private static final LocalDate JAN7  = LocalDate.of(2024, 1, 7);
    private static final LocalDate JAN10 = LocalDate.of(2024, 1, 10);
    private static final LocalDate JAN15 = LocalDate.of(2024, 1, 15);
    private static final LocalDate JAN31 = LocalDate.of(2024, 1, 31);

    // ── lengthInDays ─────────────────────────────────────────────────────────

    @Test
    void lengthInDays_oneDay() {
        assertEquals(1, DateRangeUtils.lengthInDays(JAN1, JAN1));
    }

    @Test
    void lengthInDays_week() {
        assertEquals(7, DateRangeUtils.lengthInDays(JAN1, JAN7));
    }

    @Test
    void lengthInDays_startAfterEndThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> DateRangeUtils.lengthInDays(JAN7, JAN1));
    }

    // ── overlaps ─────────────────────────────────────────────────────────────

    @Test
    void overlaps_touching() {
        assertTrue(DateRangeUtils.overlaps(JAN1, JAN7, JAN7, JAN15));
    }

    @Test
    void overlaps_noOverlap() {
        assertFalse(DateRangeUtils.overlaps(JAN1, JAN7, JAN10, JAN15));
    }

    @Test
    void overlaps_oneInsideOther() {
        assertTrue(DateRangeUtils.overlaps(JAN1, JAN31, JAN7, JAN15));
    }

    // ── intersection ─────────────────────────────────────────────────────────

    @Test
    void intersection_partial() {
        LocalDate[] result = DateRangeUtils.intersection(JAN1, JAN10, JAN7, JAN15);
        assertNotNull(result);
        assertEquals(JAN7,  result[0]);
        assertEquals(JAN10, result[1]);
    }

    @Test
    void intersection_noOverlapReturnsNull() {
        assertNull(DateRangeUtils.intersection(JAN1, JAN7, JAN10, JAN15));
    }

    // ── contains ─────────────────────────────────────────────────────────────

    @Test
    void contains_insideRange() {
        assertTrue(DateRangeUtils.contains(JAN1, JAN31, JAN15));
    }

    @Test
    void contains_onBoundary() {
        assertTrue(DateRangeUtils.contains(JAN1, JAN31, JAN1));
        assertTrue(DateRangeUtils.contains(JAN1, JAN31, JAN31));
    }

    @Test
    void contains_outside() {
        assertFalse(DateRangeUtils.contains(JAN7, JAN15, JAN1));
    }

    // ── expand ───────────────────────────────────────────────────────────────

    @Test
    void expand_byThreeDays() {
        LocalDate[] result = DateRangeUtils.expand(JAN7, JAN15, 3);
        assertEquals(LocalDate.of(2024, 1, 4),  result[0]);
        assertEquals(LocalDate.of(2024, 1, 18), result[1]);
    }

    // ── splitIntoChunks ──────────────────────────────────────────────────────

    @Test
    void splitIntoChunks_evenSplit() {
        List<LocalDate[]> chunks = DateRangeUtils.splitIntoChunks(JAN1, JAN7, 7);
        assertEquals(1, chunks.size());
        assertEquals(JAN1, chunks.get(0)[0]);
        assertEquals(JAN7, chunks.get(0)[1]);
    }

    @Test
    void splitIntoChunks_unevenSplit() {
        // 10 days split into chunks of 3 → [1..3], [4..6], [7..9], [10..10]
        List<LocalDate[]> chunks = DateRangeUtils.splitIntoChunks(JAN1, JAN10, 3);
        assertEquals(4, chunks.size());
        assertEquals(JAN10, chunks.get(3)[0]);
        assertEquals(JAN10, chunks.get(3)[1]);
    }

    // ── weekdaysInRange ──────────────────────────────────────────────────────

    @Test
    void weekdaysInRange_oneFullWeek() {
        // 2024-01-01 is Monday
        List<LocalDate> days = DateRangeUtils.weekdaysInRange(JAN1, JAN7);
        assertEquals(5, days.size());
        // Saturday and Sunday excluded
        assertFalse(days.contains(LocalDate.of(2024, 1, 6)));
        assertFalse(days.contains(LocalDate.of(2024, 1, 7)));
    }

    // ── merge ────────────────────────────────────────────────────────────────

    @Test
    void merge_overlapping() {
        LocalDate[] merged = DateRangeUtils.merge(JAN1, JAN10, JAN7, JAN15);
        assertNotNull(merged);
        assertEquals(JAN1,  merged[0]);
        assertEquals(JAN15, merged[1]);
    }

    @Test
    void merge_adjacent() {
        LocalDate[] merged = DateRangeUtils.merge(JAN1, JAN7,
                LocalDate.of(2024, 1, 8), JAN15);
        assertNotNull(merged);
        assertEquals(JAN1,  merged[0]);
        assertEquals(JAN15, merged[1]);
    }

    @Test
    void merge_gapReturnsNull() {
        // JAN1-JAN7 and JAN10-JAN15 have a gap of 2 days
        assertNull(DateRangeUtils.merge(JAN1, JAN7, JAN10, JAN15));
    }

    // ── shift ────────────────────────────────────────────────────────────────

    @Test
    void shift_forward() {
        LocalDate[] shifted = DateRangeUtils.shift(JAN1, JAN7, 7);
        assertEquals(LocalDate.of(2024, 1, 8),  shifted[0]);
        assertEquals(LocalDate.of(2024, 1, 14), shifted[1]);
    }

    @Test
    void shift_backward() {
        LocalDate[] shifted = DateRangeUtils.shift(JAN7, JAN15, -6);
        assertEquals(JAN1, shifted[0]);
        assertEquals(LocalDate.of(2024, 1, 9), shifted[1]);
    }

    // ── completeWeeks ────────────────────────────────────────────────────────

    @Test
    void completeWeeks_exactWeeks() {
        assertEquals(2, DateRangeUtils.completeWeeks(JAN1, LocalDate.of(2024, 1, 14)));
    }

    @Test
    void completeWeeks_partialWeek() {
        assertEquals(1, DateRangeUtils.completeWeeks(JAN1, JAN10));
    }
}
