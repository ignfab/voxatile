package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class LinearRing2dTest {
    private LinearRing2d ring;

    @BeforeEach
    public void init() {
        ring = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ));
    }

    @Test
    public void testFromPoints() {
        LinearRing2d ring;

        ring = assertInstanceOf(LinearRing2d.class, assertDoesNotThrow(() -> LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ))));
        assertEquals(new WorldBBox2d(1, 2, 5, 5), ring.bbox());

        ring = assertInstanceOf(LinearRing2d.class, assertDoesNotThrow(() -> LinearRing2d.fromPoints(List.of(
        ))));
        assertEquals(WorldBBox2d.EMPTY, ring.bbox());

        ring = assertInstanceOf(LinearRing2d.class, assertDoesNotThrow(() -> LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(1, 2),
            new WorldCoords2d(1, 2)
        ))));
        assertEquals(new WorldBBox2d(1, 2, 1, 1), ring.bbox());

        ring = assertInstanceOf(LinearRing2d.class, assertDoesNotThrow(() -> LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2)
        ))));
        assertEquals(WorldBBox2d.EMPTY, ring.bbox());

    }

    @Test
    public void testSize() {
        assertEquals(3, ring.size());

        ring = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2)
        ));

        assertEquals(0, ring.size());

        ring = LinearRing2d.fromPoints(List.of());

        assertEquals(0, ring.size());
    }

    @Test
    public void testGet() {
        Segment2d line;

        line = ring.get(-1);
        assertEquals(new WorldCoords2d(5, 6), line.start());
        assertEquals(new WorldCoords2d(1, 2), line.end());

        line = ring.get(0);
        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(3, 4), line.end());

        line = ring.get(1);
        assertEquals(new WorldCoords2d(3, 4), line.start());
        assertEquals(new WorldCoords2d(5, 6), line.end());

        line = ring.get(2);
        assertEquals(new WorldCoords2d(5, 6), line.start());
        assertEquals(new WorldCoords2d(1, 2), line.end());

        line = ring.get(3);
        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(3, 4), line.end());
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point2d(1, 2),
            new Point2d(3, 4),
            new Point2d(5, 6)
        ),  assertDoesNotThrow(ring::points).iterator());
    }

    @Test
    public void testLines() {
        assertBrowsesAllOnce(List.of(
            new Segment2d(new WorldCoords2d(1, 2), new WorldCoords2d(3, 4)),
            new Segment2d(new WorldCoords2d(3, 4), new WorldCoords2d(5, 6)),
            new Segment2d(new WorldCoords2d(5, 6), new WorldCoords2d(1, 2))
        ),  assertDoesNotThrow(ring::segments).iterator());
    }

    @Test
    public void testLineStrings() {
        Iterator<LineString2d> iter = assertDoesNotThrow(ring::lineStrings).iterator();
        assertEquals(ring, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPolygons() {
        assertFalse(assertDoesNotThrow(ring::polygons).iterator().hasNext());
    }
}
