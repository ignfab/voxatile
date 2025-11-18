package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class Polygon3dTest {
    private Polygon3d polygon;

    @BeforeEach
    public void init() {
        polygon = new Polygon3d(
            LinearRing3d.fromPoints(List.of(
                new WorldCoords3d(-3, 0, 5),
                new WorldCoords3d(0, 6, 5),
                new WorldCoords3d(3, 0, 5)
            )),
            LinearRing3d.fromPoints(List.of(
                new WorldCoords3d(-1, 1, 5),
                new WorldCoords3d(-1, 2, 5),
                new WorldCoords3d(-2, 1, 5)
            )),
            LinearRing3d.fromPoints(List.of(
                new WorldCoords3d(1, 1, 5),
                new WorldCoords3d(1, 2, 5),
                new WorldCoords3d(2, 1, 5)
            ))
        );
    }

    @Test
    public void testPoints() {
        assertBrowsesAllOnce(List.of(
            new Point3d(-3, 0, 5),
            new Point3d(0, 6, 5),
            new Point3d(3, 0, 5),
            new Point3d(-1, 1, 5),
            new Point3d(-1, 2, 5),
            new Point3d(-2, 1, 5),
            new Point3d(1, 1, 5),
            new Point3d(1, 2, 5),
            new Point3d(2, 1, 5)
        ),  assertDoesNotThrow(polygon::points).iterator());
    }

    @Test
    public void testLines() {
        assertBrowsesAllOnce(List.of(
            new Segment3d(new WorldCoords3d(3, 0, 5), new WorldCoords3d(-3, 0, 5)),
            new Segment3d(new WorldCoords3d(0, 6, 5), new WorldCoords3d(3, 0, 5)),
            new Segment3d(new WorldCoords3d(-3, 0, 5), new WorldCoords3d(0, 6, 5)),
            new Segment3d(new WorldCoords3d(-1, 1, 5), new WorldCoords3d(-1, 2, 5)),
            new Segment3d(new WorldCoords3d(-1, 2, 5), new WorldCoords3d(-2, 1, 5)),
            new Segment3d(new WorldCoords3d(-2, 1, 5), new WorldCoords3d(-1, 1, 5)),
            new Segment3d(new WorldCoords3d(1, 1, 5), new WorldCoords3d(2, 1, 5)),
            new Segment3d(new WorldCoords3d(2, 1, 5), new WorldCoords3d(1, 2, 5)),
            new Segment3d(new WorldCoords3d(1, 2, 5), new WorldCoords3d(1, 1, 5))
        ),  assertDoesNotThrow(polygon::segments).iterator());
    }

    @Test
    public void testLineStrings() {
        assertBrowsesAllOnce(
            List.of(
                LinearRing3d.fromPoints(List.of(
                    new WorldCoords3d(-3, 0, 5),
                    new WorldCoords3d(0, 6, 5),
                    new WorldCoords3d(3, 0, 5)
                )),
                LinearRing3d.fromPoints(List.of(
                    new WorldCoords3d(-1, 1, 5),
                    new WorldCoords3d(-1, 2, 5),
                    new WorldCoords3d(-2, 1, 5)
                )),
                LinearRing3d.fromPoints(List.of(
                    new WorldCoords3d(2, 1, 5),
                    new WorldCoords3d(1, 2, 5),
                    new WorldCoords3d(1, 1, 5)
                ))
            ),
            assertDoesNotThrow(polygon::lineStrings).iterator()
        );
    }

    @Test
    public void testPolygons() {
        Iterator<Polygon3d> iter = assertDoesNotThrow(polygon::polygons).iterator();
        assertEquals(polygon, assertDoesNotThrow(iter::next));
        assertFalse(iter.hasNext());
    }
}
