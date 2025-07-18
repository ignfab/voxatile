package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SegmentCapabilityTest {
    @Test
    public void testConstructor() {
        // Non-Expendable
        // Negative length
        assertThrows(IllegalArgumentException.class, () -> new SegmentCapability.NonExpendable(1, -4));
        assertDoesNotThrow(() -> new SegmentCapability.NonExpendable(-1, 3));

        // Expendable
        // Testing that inconsistent extendableIndexCoordinate throws Exception
        // -3 -2 -1  0  1  2  3
        //    |-  -  -  - -|
        assertThrows(IllegalArgumentException.class, () -> new SegmentCapability.Expendable(-2, -3, 4));
        assertThrows(IllegalArgumentException.class, () -> new SegmentCapability.Expendable(-2, 3, 4));
        assertDoesNotThrow(() -> new SegmentCapability.Expendable(-2, -2, 4));
        assertDoesNotThrow(() -> new SegmentCapability.Expendable(-2, 0, 4));
        assertDoesNotThrow(() -> new SegmentCapability.Expendable(-2, 2, 4));
    }

    @Test
    public void testAtIndex() {
        // Negative arguments for atIndex()
        SegmentCapability capability = new SegmentCapability.NonExpendable(0, 3);
        assertThrows(IllegalArgumentException.class, () -> capability.atIndex(-1, 3));
        assertThrows(IllegalArgumentException.class, () -> capability.atIndex(1, -3));

        // Non-expendable

        // Non-expendable without offset
        //  0  1  2
        //  a  b  c
        //  0  1  2
        SegmentCapability unexpendable = new SegmentCapability.NonExpendable(0, 3);
        assertEquals(0, unexpendable.atIndex(0, 3));
        assertEquals(1, unexpendable.atIndex(1, 3));
        assertEquals(2, unexpendable.atIndex(2, 3));
        // Smaller wanted length
        assertEquals(1, unexpendable.atIndex(1, 2));
        assertThrows(IllegalArgumentException.class, () -> unexpendable.atIndex(3, 3));

        // Non-expendable with negative offset
        // -2 -1  0
        //  a  b  c
        //  0  1  2
        SegmentCapability unexpendableNegativeOffset = new SegmentCapability.NonExpendable(-2, 3);
        assertEquals(-2, unexpendableNegativeOffset.atIndex(0, 3));
        assertEquals(0, unexpendableNegativeOffset.atIndex(2, 3));

        // Non-expendable with positive offset
        //  3  4  5
        //  a  b  c
        //  0  1  2
        SegmentCapability unexpendablePositiveOffset = new SegmentCapability.NonExpendable(3, 4);
        assertEquals(3, unexpendablePositiveOffset.atIndex(0, 4));
        assertEquals(6, unexpendablePositiveOffset.atIndex(3, 4));

        // Expendable

        // 4  5  6  7
        // a  R  b  c
        SegmentCapability expendable = new SegmentCapability.Expendable(4, 5, 3);
        // When shrunk (wantedLength = 3)
        // 4  6  7
        // a  b  c
        // 0  1  2
        assertEquals(4, expendable.atIndex(0, 3));
        assertEquals(6, expendable.atIndex(1, 3));
        assertEquals(7, expendable.atIndex(2, 3));
        // wantedLength < minimalLength
        assertEquals(4, expendable.atIndex(0, 1));
        assertEquals(4, expendable.atIndex(0, 2));
        assertEquals(6, expendable.atIndex(1, 2));

        // When extended, here wantedLength = 6
        // 4  5  5  5  6  7
        // a  R  R  R  b  c
        // 0  1  2  3  4  5
        assertEquals(4, expendable.atIndex(0, 6));
        assertEquals(6, expendable.atIndex(4, 6));
        assertEquals(5, expendable.atIndex(1, 6));
        assertEquals(5, expendable.atIndex(3, 6));

        // extendableIndexCoordinate at left bound
        // -2 -1  0  1
        //  R  a  b  c
        SegmentCapability expendableLeft = new SegmentCapability.Expendable(-2, -2, 3);
        // -1  0  1
        //  a  b  c
        //  0  1  2
        assertEquals(-1, expendableLeft.atIndex(0, 3));
        assertEquals(1, expendableLeft.atIndex(2, 3));
        // -2 -2 -2 -1  0  1
        //  R  R  R  a  b  c
        //  0  1  2  3  4  5
        assertEquals(-2, expendableLeft.atIndex(0, 6));
        assertEquals(-2, expendableLeft.atIndex(2, 6));
        assertEquals(-1, expendableLeft.atIndex(3, 6));

        // extendableIndexCoordinate at right bound
        // -1  0  1  2
        //  a  b  c  R
        SegmentCapability expendableRight = new SegmentCapability.Expendable(-1, 2, 3);
        // -1  0  1  2
        //  a  b  c  R
        //  0  1  2  3
        assertEquals(-1, expendableRight.atIndex(0, 3));
        assertEquals(1, expendableRight.atIndex(2, 3));
        // -1  0  1  2  2  2
        //  a  b  c  R  R  R
        //  0  1  2  3  4  5
        assertEquals(2, expendableRight.atIndex(3, 6));
        assertEquals(2, expendableRight.atIndex(5, 6));
    }
}
