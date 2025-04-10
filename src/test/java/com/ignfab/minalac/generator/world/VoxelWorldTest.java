package com.ignfab.minalac.generator.world;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

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
        public void save(File destination) throws MapWriteException {}

        @Override
        public VoxelWorldTile newTile(WorldBBox3d limits) {
            throw new UnsupportedOperationException("Unimplemented method 'newTile'");
        }
    }
}
