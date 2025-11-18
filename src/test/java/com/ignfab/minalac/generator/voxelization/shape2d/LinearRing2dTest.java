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
        Segment2d segment;

        segment = ring.get(-1);
        assertEquals(new WorldCoords2d(5, 6), segment.start());
        assertEquals(new WorldCoords2d(1, 2), segment.end());

        segment = ring.get(0);
        assertEquals(new WorldCoords2d(1, 2), segment.start());
        assertEquals(new WorldCoords2d(3, 4), segment.end());

        segment = ring.get(1);
        assertEquals(new WorldCoords2d(3, 4), segment.start());
        assertEquals(new WorldCoords2d(5, 6), segment.end());

        segment = ring.get(2);
        assertEquals(new WorldCoords2d(5, 6), segment.start());
        assertEquals(new WorldCoords2d(1, 2), segment.end());

        segment = ring.get(3);
        assertEquals(new WorldCoords2d(1, 2), segment.start());
        assertEquals(new WorldCoords2d(3, 4), segment.end());
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

    @Test
    public void testToClockwise() {
        // Regular triangle
        WorldCoords2d a = new WorldCoords2d(1, 3);
        WorldCoords2d b = new WorldCoords2d(5, 3);
        WorldCoords2d c = new WorldCoords2d(3, 5);

        LinearRing2d counterClockwise = LinearRing2d.fromPoints(a, b, c);
        assertEquals(new Segment2d(a, b), counterClockwise.get(0));
        assertEquals(new Segment2d(b, c), counterClockwise.get(1));
        assertEquals(new Segment2d(c, a), counterClockwise.get(2));

        LinearRing2d clockwise = counterClockwise.toClockwise();
        assertNotEquals(counterClockwise.get(0), clockwise.get(0));
        assertEquals(new Segment2d(c, b), clockwise.get(0));

        assertNotEquals(counterClockwise.get(1), clockwise.get(1));
        assertEquals(new Segment2d(b, a), clockwise.get(1));

        assertNotEquals(counterClockwise.get(2), clockwise.get(2));
        assertEquals(new Segment2d(a, c), clockwise.get(2));

        clockwise = clockwise.toClockwise();
        assertNotEquals(counterClockwise.get(0), clockwise.get(0));
        assertEquals(new Segment2d(c, b), clockwise.get(0));

        assertNotEquals(counterClockwise.get(1), clockwise.get(1));
        assertEquals(new Segment2d(b, a), clockwise.get(1));

        assertNotEquals(counterClockwise.get(2), clockwise.get(2));
        assertEquals(new Segment2d(a, c), clockwise.get(2));
    }

    public void testToCounterClockwise() {
        // Regular triangle
        WorldCoords2d a = new WorldCoords2d(3, 5);
        WorldCoords2d b = new WorldCoords2d(5, 3);
        WorldCoords2d c = new WorldCoords2d(1, 3);

        LinearRing2d clockwise = LinearRing2d.fromPoints(a, b, c);
        assertEquals(new Segment2d(a, b), clockwise.get(0));
        assertEquals(new Segment2d(b, c), clockwise.get(1));
        assertEquals(new Segment2d(c, a), clockwise.get(2));

        LinearRing2d counterClockwise = clockwise.toCounterClockwise();
        assertNotEquals(clockwise.get(0), counterClockwise.get(0));
        assertEquals(new Segment2d(c, b), counterClockwise.get(0));

        assertNotEquals(clockwise.get(1), counterClockwise.get(1));
        assertEquals(new Segment2d(b, a), counterClockwise.get(1));

        assertNotEquals(clockwise.get(2), counterClockwise.get(2));
        assertEquals(new Segment2d(a, c), counterClockwise.get(2));

        counterClockwise = counterClockwise.toCounterClockwise();
        assertNotEquals(clockwise.get(0), counterClockwise.get(0));
        assertEquals(new Segment2d(c, b), counterClockwise.get(0));

        assertNotEquals(clockwise.get(1), counterClockwise.get(1));
        assertEquals(new Segment2d(b, a), counterClockwise.get(1));

        assertNotEquals(clockwise.get(2), counterClockwise.get(2));
        assertEquals(new Segment2d(a, c), counterClockwise.get(2));
    }

    @Test
    public void testInvert() {
        // Regular triangle
        WorldCoords2d a = new WorldCoords2d(3, 5);
        WorldCoords2d b = new WorldCoords2d(5, 3);
        WorldCoords2d c = new WorldCoords2d(1, 3);

        LinearRing2d clockwise = LinearRing2d.fromPoints(a, b, c);
        assertEquals(new Segment2d(a, b), clockwise.get(0));
        assertEquals(new Segment2d(b, c), clockwise.get(1));
        assertEquals(new Segment2d(c, a), clockwise.get(2));

        LinearRing2d counterClockwise = clockwise.invert();
        assertNotEquals(clockwise.get(0), counterClockwise.get(0));
        assertEquals(new Segment2d(c, b), counterClockwise.get(0));

        assertNotEquals(clockwise.get(1), counterClockwise.get(1));
        assertEquals(new Segment2d(b, a), counterClockwise.get(1));

        assertNotEquals(clockwise.get(2), counterClockwise.get(2));
        assertEquals(new Segment2d(a, c), counterClockwise.get(2));

        clockwise = counterClockwise.invert();
        assertEquals(new Segment2d(a, b), clockwise.get(0));
        assertEquals(new Segment2d(b, c), clockwise.get(1));
        assertEquals(new Segment2d(c, a), clockwise.get(2));
    }

    @Test
    public void testIsClockwise() {
        // Counter-clockwise polygon.
        //
        // Vertices where the determinant equals 0:
        //         Point1   | Point2   | Point3
        // Vertex1: -1, 1   | -3, 3    | -2, 2
        // Vertex2: -1, 0   | -3, -2   | -2, -1
        // Vertex3:  0, 0   |  2, -2   |  1, -1
        // Vertex4:  0, 1   |  2, 3    |  1, 2
        LinearRing2d polygon = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(-1, 1),
            new WorldCoords2d(-3, 3),
            new WorldCoords2d(-2, 2),
            new WorldCoords2d(-3, 2),
            new WorldCoords2d(-3, 0),
            new WorldCoords2d(-1, 0),
            new WorldCoords2d(-3, -2),
            new WorldCoords2d(-2, -1),
            new WorldCoords2d(-2, -2),
            new WorldCoords2d(0, -2),
            new WorldCoords2d(0, 0),
            new WorldCoords2d(2, -2),
            new WorldCoords2d(1, -1),
            new WorldCoords2d(2, -1),
            new WorldCoords2d(2, 1),
            new WorldCoords2d(0, 1),
            new WorldCoords2d(2, 3),
            new WorldCoords2d(1, 2),
            new WorldCoords2d(1, 3),
            new WorldCoords2d(-1, 3)
        ));
        assertFalse(polygon.isClockwise());
        assertTrue(polygon.invert().isClockwise());

        // A regular concave polygon, nothing special.
        LinearRing2d concave = LinearRing2d.fromPoints(
            new WorldCoords2d(2, 1),
            new WorldCoords2d(1, -6),
            new WorldCoords2d(0, -1),
            new WorldCoords2d(-4, -2),
            new WorldCoords2d(-3, 2),
            new WorldCoords2d(1, 3)
        );
        assertTrue(concave.isClockwise());
        assertFalse(concave.invert().isClockwise());

        LinearRing2d flat = LinearRing2d.fromPoints(
            new WorldCoords2d(-4, 5),
            new WorldCoords2d(4, 5)
        );
        assertFalse(flat.isClockwise());
        assertFalse(flat.invert().isClockwise());

        flat = LinearRing2d.fromPoints(
            new WorldCoords2d(-4, 5),
            new WorldCoords2d(4, 5),
            new WorldCoords2d(9, 5)
        );
        assertFalse(flat.isClockwise());
        assertFalse(flat.invert().isClockwise());
    }
}
