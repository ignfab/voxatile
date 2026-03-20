package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class LinearRing3dTest {
    private LinearRing3d ring;

    @BeforeEach
    public void init() {
        ring = LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));
    }

    @Test
    public void testFromPoints() {
        LinearRing3d ring;

        ring = assertInstanceOf(LinearRing3d.class, assertDoesNotThrow(() -> LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ))));
        assertEquals(new WorldBBox3d(1, 2, 3, 7, 7, 7), ring.bbox());

        ring = assertInstanceOf(LinearRing3d.class, assertDoesNotThrow(() -> LinearRing3d.fromPoints(List.of(
        ))));
        assertEquals(WorldBBox3d.EMPTY, ring.bbox());

        ring = assertInstanceOf(LinearRing3d.class, assertDoesNotThrow(() -> LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, 3)
        ))));
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), ring.bbox());

        ring = assertInstanceOf(LinearRing3d.class, assertDoesNotThrow(() -> LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3)
        ))));
        assertEquals(WorldBBox3d.EMPTY, ring.bbox());

    }

    @Test
    public void testSize() {
        assertEquals(3, ring.size());

        ring = LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3)
        ));

        assertEquals(0, ring.size());

        ring = LinearRing3d.fromPoints(List.of());

        assertEquals(0, ring.size());
    }

    @Test
    public void testGet() {
        Segment3d segment;

        segment = ring.get(-1);
        assertEquals(new WorldCoords3d(7, 8, 9), segment.start());
        assertEquals(new WorldCoords3d(1, 2, 3), segment.end());

        segment = ring.get(0);
        assertEquals(new WorldCoords3d(1, 2, 3), segment.start());
        assertEquals(new WorldCoords3d(4, 5, 6), segment.end());

        segment = ring.get(1);
        assertEquals(new WorldCoords3d(4, 5, 6), segment.start());
        assertEquals(new WorldCoords3d(7, 8, 9), segment.end());

        segment = ring.get(2);
        assertEquals(new WorldCoords3d(7, 8, 9), segment.start());
        assertEquals(new WorldCoords3d(1, 2, 3), segment.end());

        segment = ring.get(3);
        assertEquals(new WorldCoords3d(1, 2, 3), segment.start());
        assertEquals(new WorldCoords3d(4, 5, 6), segment.end());
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point3d(1, 2, 3),
            new Point3d(4, 5, 6),
            new Point3d(7, 8, 9)
        ),  assertDoesNotThrow(ring::points).iterator());
    }

    @Test
    public void testLineStrings() {
        Iterator<LineString3d> iter = assertDoesNotThrow(ring::lineStrings).iterator();
        assertEquals(ring, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPolygons() {
        // Ring can be seen as a polygon, so it has only one polygon.
        // Polygon unique line string may be inverted so we cannot compare it.
        Iterator<Polygon3d> iter = assertDoesNotThrow(ring::polygons).iterator();
        assertDoesNotThrow(iter::next);
        assertFalse(iter.hasNext());
    }
}
