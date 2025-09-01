package com.ignfab.minalac.generator.generation;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;


public class GenerationTileTest {

    @Test
    public void testModelTypeVolumeMethod() {
        Generation generation = new Generation(
            new TestingVoxelWorld(),
            null, // Seed
            null, // CRS
            0.0, 0.0, // Center
            100, 100, // extent
            1.0, 1.0, // Scales
            0, // Angle
            100 // Max tile size)
        );

        GenerationTile tile = new GenerationTile(generation, WorldBBox3d.ORIGIN);
        GenerationTile.ModelTypeVolume volume;

        volume = assertInstanceOf(GenerationTile.ModelTypeVolume.class,
            assertDoesNotThrow(() -> tile.modelTypeVolume("TOTO"))
        );
        assertEquals(WorldBBox3d.ORIGIN, volume.get());
    }

    @Test
    public void testModelTypeVolumeClass() {
        GenerationTile.ModelTypeVolume volume = new GenerationTile.ModelTypeVolume(new WorldBBox3d(0, 0, 0, 5, 5, 5));

        assertDoesNotThrow(() -> volume.include(new WorldBBox2d(-5, 0, 1, 1)));
        assertDoesNotThrow(() -> volume.include(new WorldBBox3d(-2, -2, -2, 5, 5, 5)));

        WorldBBox3d bbox = assertDoesNotThrow(() -> volume.get());
        assertEquals(new WorldBBox3d(-5, -2, -2, 10, 7, 7), bbox);

        assertThrows(IllegalStateException.class, () -> volume.include(new WorldBBox3d(-2, -2, -2, 5, 5, 5)));
    }
}
