package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VoxelWorldTest {
    @Test
    public void testSetLimits() {
        VoxelWorld world = new VoxelWorldMock();
        assertThrows(IllegalArgumentException.class, () -> {
            world.setLimits(new WorldBBox3d(
                    new WorldCoords3d(-21, -19, -18),
                    new WorldCoords3d(21, 22, 23)
            ));
        });

        assertDoesNotThrow(() -> {
            world.setLimits(new WorldBBox3d(
                new WorldCoords3d(-1, -2, -3),
                new WorldCoords3d(4, 5, 6))
            );
        });

        assertEquals(new WorldBBox3d(new WorldCoords3d(-1, -2, -3), new WorldCoords3d(4, 5, 6)), world.limits());

        assertThrows(IllegalStateException.class, () -> {
            world.setLimits(new WorldBBox3d(
                new WorldCoords3d(-1, -2, -3),
                new WorldCoords3d(7, 8, 9))
            );
        });

    }
    private static class VoxelWorldMock extends VoxelWorld {

        protected VoxelWorldMock() {
            super(null);
        }

        @Override
        public WorldBBox3d maxLimits() {
            return new WorldBBox3d(
                new WorldCoords3d(-20, -19, -18),
                new WorldCoords3d(21, 22, 23)
            );
        }

        @Override
        public VoxelTypeFactory getFactory() {
            return null;
        }

        @Override
        public void save(File destination) throws MapWriteException {}
    }
}
