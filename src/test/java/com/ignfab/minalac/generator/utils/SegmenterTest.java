package com.ignfab.minalac.generator.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SegmenterTest {
    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Segmenter(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Segmenter(-1, 10));

        assertThrows(IllegalArgumentException.class, () -> new Segmenter(3, 0));
        assertThrows(IllegalArgumentException.class, () -> new Segmenter(4, -1));

        assertThrows(IllegalArgumentException.class, () -> new Segmenter(5, 10));
    }

    @Test
    public void testGetSegment() {
        Segmenter noRemainder = new Segmenter(9, 3);
        // size: 3 - 3 - 3
        assertEquals(new Segmenter.Segment(0, 3), noRemainder.getSegment(0));
        assertEquals(new Segmenter.Segment(0 + 1, 3), noRemainder.getSegment(3 + 1));
        assertEquals(new Segmenter.Segment(0 + 2, 3), noRemainder.getSegment(6 + 2));

        // Should split it in this way
        // i: 0 1 - 2 3 4 - 5 6
        Segmenter centerFirst = new Segmenter(7, 2);

        // i: 0 1 - 2 3 4 - 5 6
        // i: 0 1 -       -
        assertEquals(new Segmenter.Segment(0, 2), centerFirst.getSegment(0));
        assertEquals(new Segmenter.Segment(1, 2), centerFirst.getSegment(1));

        // i: 0 1 - 2 3 4 - 5 6
        // i:    - 0 1 2 -
        assertEquals(new Segmenter.Segment(0, 3), centerFirst.getSegment(2));
        assertEquals(new Segmenter.Segment(1, 3), centerFirst.getSegment(3));
        assertEquals(new Segmenter.Segment(2, 3), centerFirst.getSegment(4));

        // i: 0 1 - 2 3 4 - 5 6
        // i:             - 0 1
        assertEquals(new Segmenter.Segment(0, 2), centerFirst.getSegment(5));
        assertEquals(new Segmenter.Segment(1, 2), centerFirst.getSegment(6));

        Segmenter even = new Segmenter(46, 10);
        // Remaining is 6, distribution should look like this
        // size: 11 - 12 - 12 - 11
        assertEquals(new Segmenter.Segment(0, 11), even.getSegment(0));
        assertEquals(new Segmenter.Segment(0 + 1, 12), even.getSegment(11 + 1));
        assertEquals(new Segmenter.Segment(0 + 2, 12), even.getSegment(23 + 2));
        assertEquals(new Segmenter.Segment(0 + 3, 11), even.getSegment(35 + 3));

        Segmenter odd = new Segmenter(58, 10);
        // Remaining is 8, distribution should look like this
        // size: 11 - 12 - 12 - 12 - 11
        assertEquals(new Segmenter.Segment(0, 11), odd.getSegment(0));
        assertEquals(new Segmenter.Segment(0 + 1, 12), odd.getSegment(11 + 1));
        assertEquals(new Segmenter.Segment(0 + 2, 12), odd.getSegment(23 + 2));
        assertEquals(new Segmenter.Segment(0 + 3, 12), odd.getSegment(35 + 3));
        assertEquals(new Segmenter.Segment(0 + 4, 11), odd.getSegment(47 + 4));
    }
}
