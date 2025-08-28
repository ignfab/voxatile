package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class LinearRing2dTest {
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
        LinearRing2d string;

        string = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ));

        assertEquals(3, string.size());

        string = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2)
        ));

        assertEquals(0, string.size());

        string = LinearRing2d.fromPoints(List.of());

        assertEquals(0, string.size());
    }

    @Test
    public void testGet() {
        LinearRing2d string = LinearRing2d.fromPoints(List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(3, 4),
            new WorldCoords2d(5, 6)
        ));

        Line2d line;

        line = string.get(-1);
        assertEquals(new WorldCoords2d(5, 6), line.start());
        assertEquals(new WorldCoords2d(1, 2), line.end());

        line = string.get(0);
        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(3, 4), line.end());

        line = string.get(1);
        assertEquals(new WorldCoords2d(3, 4), line.start());
        assertEquals(new WorldCoords2d(5, 6), line.end());

        line = string.get(2);
        assertEquals(new WorldCoords2d(5, 6), line.start());
        assertEquals(new WorldCoords2d(1, 2), line.end());

        line = string.get(3);
        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(3, 4), line.end());

    }
}
