package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class LineString3dTest {
    private LineString3d string;

    @BeforeEach
    public void init() {
        string = LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));
    }

    @Test
    public void testFromPoints() {
        LineString3d string;

        string = assertInstanceOf(LineString3d.class, assertDoesNotThrow(() -> LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ))));
        assertEquals(new WorldBBox3d(1, 2, 3, 7, 7, 7), string.bbox());

        string = assertInstanceOf(LineString3d.class, assertDoesNotThrow(() -> LineString3d.fromPoints(List.of(
        ))));
        assertEquals(WorldBBox3d.EMPTY, string.bbox());

        string = assertInstanceOf(LineString3d.class, assertDoesNotThrow(() -> LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, 3)
        ))));
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), string.bbox());

        string = assertInstanceOf(LineString3d.class, assertDoesNotThrow(() -> LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3)
        ))));
        assertEquals(WorldBBox3d.EMPTY, string.bbox());

    }

    @Test
    public void testSize() {
        assertEquals(2, string.size());

        string = LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3)
        ));

        assertEquals(0, string.size());

        string = LineString3d.fromPoints(List.of());

        assertEquals(0, string.size());
    }

    @Test
    public void testGet() {
        Segment3d segment;

        segment = string.get(0);
        assertEquals(new WorldCoords3d(1, 2, 3), segment.start());
        assertEquals(new WorldCoords3d(4, 5, 6), segment.end());

        segment = string.get(1);
        assertEquals(new WorldCoords3d(4, 5, 6), segment.start());
        assertEquals(new WorldCoords3d(7, 8, 9), segment.end());

        assertNull(string.get(-1));
        assertNull(string.get(2));
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point3d(1, 2, 3),
            new Point3d(4, 5, 6),
            new Point3d(7, 8, 9)
        ),  assertDoesNotThrow(string::points).iterator());
    }

    @Test
    public void testLineStrings() {
        Iterator<LineString3d> iter = assertDoesNotThrow(string::lineStrings).iterator();
        assertEquals(string, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPolygons() {
        assertFalse(assertDoesNotThrow(string::polygons).iterator().hasNext());
    }
}
