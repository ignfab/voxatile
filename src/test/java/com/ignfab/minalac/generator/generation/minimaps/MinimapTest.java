package com.ignfab.minalac.generator.generation.minimaps;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class MinimapTest {
    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 1, 1), 1000));
    }

    @Test
    void testSize() {
        Minimap minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 2, 2), 2));
        assertEquals(2, minimap.getWidth());
        assertEquals(2, minimap.getHeight());

        minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 20, 22), 2));
        assertEquals(2, minimap.getWidth());
        assertEquals(2, minimap.getHeight());

        minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 20, 40), 2));
        assertEquals(1, minimap.getWidth());
        assertEquals(2, minimap.getHeight());

        minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 1, 1), 2));
        assertEquals(2, minimap.getWidth());
        assertEquals(2, minimap.getHeight());

        minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 1, 1), 0));
        assertEquals(0, minimap.getWidth());
        assertEquals(0, minimap.getHeight());

        minimap = assertDoesNotThrow(() -> new Minimap(new WorldBBox2d(0, 0, 0, 0), 0));
        assertEquals(0, minimap.getWidth());
        assertEquals(0, minimap.getHeight());
    }

    @Test
    void testAveragingSamePixel() {
        Minimap minimap = new Minimap(
            new WorldBBox2d(0, 0, 1, 1),
            1
        );

        minimap.add(new Color(255, 0, 0), new WorldCoords3d(0, 0, 0));
        minimap.add(new Color(0, 255, 0), new WorldCoords3d(0, 0, 0));
        minimap.add(new Color(0, 0, 255), new WorldCoords3d(0, 0, 0));

        MinimapCell actual = minimap.get(0, 0);
        assertEquals(85, actual.getRed(), 0.01);
        assertEquals(85, actual.getGreen(), 0.01);
        assertEquals(85, actual.getBlue(), 0.01);

        minimap = new Minimap(
            new WorldBBox2d(0, 0, 2, 2),
            1
        );

        minimap.add(new Color(255, 255, 255), new WorldCoords3d(0, 0, 0));
        minimap.add(new Color(0, 0, 0), new WorldCoords3d(1, 0, 0));
        minimap.add(new Color(0, 0, 0), new WorldCoords3d(0, 1, 0));
        minimap.add(new Color(0, 0, 0), new WorldCoords3d(1, 1, 0));

        assertEquals(63.75, minimap.get(0, 0).getRed(), 0.01);
        assertEquals(63.75, minimap.get(0, 0).getGreen(), 0.01);
        assertEquals(63.75, minimap.get(0, 0).getBlue(), 0.01);
    }

    @Test
    void testInterpolatedWeighting() {
        Minimap minimap = new Minimap(new WorldBBox2d(0, 0, 3, 2), 2);
        minimap.add(new Color(255, 255, 255), new WorldCoords3d(1, 0, 0));
        assertEquals(255, minimap.get(0, 0).getRed());
        assertEquals(255, minimap.get(1, 0).getRed());

        minimap = new Minimap(new WorldBBox2d(0, 0, 3, 2), 2);
        minimap.add(new Color(0, 0, 0), new WorldCoords3d(0, 0, 0));
        minimap.add(new Color(255, 255, 255), new WorldCoords3d(1, 0, 0));
        assertEquals(85, minimap.get(0, 0).getRed(), 0.01);
        assertEquals(85, minimap.get(0, 0).getGreen(), 0.01);
        assertEquals(85, minimap.get(0, 0).getBlue(), 0.01);
    }

    @Test
    void testSingleVoxelNoSampling() {
        Minimap minimap = new Minimap(new WorldBBox2d(0, 0, 2, 2), 2);
        minimap.add(new Color(255, 0, 0), new WorldCoords3d(0, 0, 10));
        minimap.add(new Color(0, 0, 255), new WorldCoords3d(1, 0, 8));
        minimap.add(new Color(0, 255, 0), new WorldCoords3d(0, 1, 15));
        minimap.add(new Color(0, 0, 0), new WorldCoords3d(1, 1, 6));
        assertEquals(255, minimap.get(0, 0).getRed(), 0.01);
        assertEquals(0, minimap.get(0, 0).getGreen(), 0.01);
        assertEquals(0, minimap.get(0, 0).getBlue(), 0.01);
        assertEquals(10.0, minimap.get(0, 0).getHeight(), 0.01);
        assertEquals(0, minimap.get(1, 0).getRed(), 0.01);
        assertEquals(0, minimap.get(1, 0).getGreen(), 0.01);
        assertEquals(255, minimap.get(1, 0).getBlue(), 0.01);
        assertEquals(8.0, minimap.get(1, 0).getHeight(), 0.01);
        assertEquals(0, minimap.get(0, 1).getRed(), 0.01);
        assertEquals(255, minimap.get(0, 1).getGreen(), 0.01);
        assertEquals(0, minimap.get(0, 1).getBlue(), 0.01);
        assertEquals(15.0, minimap.get(0, 1).getHeight(), 0.01);
        assertEquals(0, minimap.get(1, 1).getRed(), 0.01);
        assertEquals(0, minimap.get(1, 1).getGreen(), 0.01);
        assertEquals(0, minimap.get(1, 1).getBlue(), 0.01);
        assertEquals(6.0, minimap.get(1, 1).getHeight(), 0.01);
    }
}
