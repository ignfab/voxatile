package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class LineString2dTest {
    private LineString2d string;

    @BeforeEach
    public void init() {
        string = LineString2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ));
    }

    @Test
    public void testFromPoints() {
        string = assertInstanceOf(LineString2d.class, assertDoesNotThrow(() -> LineString2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ))));
        assertEquals(new WorldBBox2d(1, 2, 5, 5), string.bbox());

        string = assertInstanceOf(LineString2d.class, assertDoesNotThrow(() -> LineString2d.fromPoints(List.of(
        ))));
        assertEquals(WorldBBox2d.EMPTY, string.bbox());

        string = assertInstanceOf(LineString2d.class, assertDoesNotThrow(() -> LineString2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(1, 2),
            new WorldCoords2d(1, 2)
        ))));
        assertEquals(new WorldBBox2d(1, 2, 1, 1), string.bbox());

        string = assertInstanceOf(LineString2d.class, assertDoesNotThrow(() -> LineString2d.fromPoints(List.of(
            new WorldCoords2d(1, 2)
        ))));
        assertEquals(WorldBBox2d.EMPTY, string.bbox());
    }

    @Test
    public void testSize() {
        assertEquals(2, string.size());

        string = LineString2d.fromPoints(List.of(
            new WorldCoords2d(1, 2)
        ));

        assertEquals(0, string.size());

        string = LineString2d.fromPoints(List.of());

        assertEquals(0, string.size());
    }

    @Test
    public void testGet() {
        Segment2d line;

        line = string.get(0);
        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(3, 4), line.end());

        line = string.get(1);
        assertEquals(new WorldCoords2d(3, 4), line.start());
        assertEquals(new WorldCoords2d(5, 6), line.end());

        assertNull(string.get(-1));
        assertNull(string.get(2));
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point2d(1, 2),
            new Point2d(3, 4),
            new Point2d(5, 6)
        ),  assertDoesNotThrow(string::points).iterator());
    }

    @Test
    public void testSegments() {
        assertBrowsesAllOnce(List.of(
            new Segment2d(new WorldCoords2d(1, 2), new WorldCoords2d(3, 4)),
            new Segment2d(new WorldCoords2d(3, 4), new WorldCoords2d(5, 6))
        ),  assertDoesNotThrow(string::segments).iterator());
    }

    @Test
    public void testLineStrings() {
        Iterator<LineString2d> iter = assertDoesNotThrow(string::lineStrings).iterator();
        assertEquals(string, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPolygons() {
        assertFalse(assertDoesNotThrow(string::polygons).iterator().hasNext());
    }
}
