package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class LineString3dTest {
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
        LineString3d string;

        string = LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));

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
        LineString3d string = LineString3d.fromPoints(List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6),
            new WorldCoords3d(7, 8, 9)
        ));

        Line3d line;

        line = string.get(0);
        assertEquals(new WorldCoords3d(1, 2, 3), line.start());
        assertEquals(new WorldCoords3d(4, 5, 6), line.end());

        line = string.get(1);
        assertEquals(new WorldCoords3d(4, 5, 6), line.start());
        assertEquals(new WorldCoords3d(7, 8, 9), line.end());

        assertNull(string.get(-1));
        assertNull(string.get(2));
    }
}
