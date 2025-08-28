package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class Polygon2dTest {
    private Polygon2d polygon;

    @BeforeEach
    public void init() {
        polygon = new Polygon2d(
            LinearRing2d.fromPoints(List.of(
                new WorldCoords2d(-3, 0),
                new WorldCoords2d(0, 6),
                new WorldCoords2d(3, 0)
            )),
            LinearRing2d.fromPoints(List.of(
                new WorldCoords2d(-1, 1),
                new WorldCoords2d(-1, 2),
                new WorldCoords2d(-2, 1)
            )),
            LinearRing2d.fromPoints(List.of(
                new WorldCoords2d(1, 1),
                new WorldCoords2d(1, 2),
                new WorldCoords2d(2, 1)
            ))
        );
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point2d(-3, 0),
            new Point2d(0, 6),
            new Point2d(3, 0),
            new Point2d(-1, 1),
            new Point2d(-1, 2),
            new Point2d(-2, 1),
            new Point2d(1, 1),
            new Point2d(1, 2),
            new Point2d(2, 1)
        ),  assertDoesNotThrow(polygon::points).iterator());
    }

    @Test
    public void testLineStrings() {
        assertBrowsesAllOnce(List.of(
                LinearRing2d.fromPoints(List.of(
                    new WorldCoords2d(-3, 0),
                    new WorldCoords2d(0, 6),
                    new WorldCoords2d(3, 0)
                )),
                LinearRing2d.fromPoints(List.of(
                    new WorldCoords2d(-1, 1),
                    new WorldCoords2d(-1, 2),
                    new WorldCoords2d(-2, 1)
                )),
                LinearRing2d.fromPoints(List.of(
                    new WorldCoords2d(1, 1),
                    new WorldCoords2d(1, 2),
                    new WorldCoords2d(2, 1)
                ))
            ),
            assertDoesNotThrow(polygon::lineStrings).iterator()
        );
    }

    @Test
    public void testPolygons() {
        Iterator<Polygon2d> iter = assertDoesNotThrow(polygon::polygons).iterator();
        assertEquals(polygon, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }
}
