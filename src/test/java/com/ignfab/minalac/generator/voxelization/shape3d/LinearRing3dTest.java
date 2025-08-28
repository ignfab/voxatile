package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class LinearRing3dTest {
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
        LinearRing3d string;

        string = LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));

        assertEquals(3, string.size());

        string = LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3)
        ));

        assertEquals(0, string.size());

        string = LinearRing3d.fromPoints(List.of());

        assertEquals(0, string.size());
    }

    @Test
    public void testGet() {
        LinearRing3d string = LinearRing3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));

        Line3d line;

        line = string.get(-1);
        assertEquals(new WorldCoords3d(7, 8, 9), line.start());
        assertEquals(new WorldCoords3d(1, 2, 3), line.end());

        line = string.get(0);
        assertEquals(new WorldCoords3d(1, 2, 3), line.start());
        assertEquals(new WorldCoords3d(4, 5, 6), line.end());

        line = string.get(1);
        assertEquals(new WorldCoords3d(4, 5, 6), line.start());
        assertEquals(new WorldCoords3d(7, 8, 9), line.end());

        line = string.get(2);
        assertEquals(new WorldCoords3d(7, 8, 9), line.start());
        assertEquals(new WorldCoords3d(1, 2, 3), line.end());

        line = string.get(3);
        assertEquals(new WorldCoords3d(1, 2, 3), line.start());
        assertEquals(new WorldCoords3d(4, 5, 6), line.end());

    }
}
