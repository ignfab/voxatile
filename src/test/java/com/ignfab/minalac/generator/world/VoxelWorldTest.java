package com.ignfab.minalac.generator.world;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.placeables.VoxelType;
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

    @Test
    public void testVoxelIterator() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(new WorldCoords3d(-1, -2, -5), new WorldCoords3d(2, 3, 6)));
        VoxelType a = new TestingVoxelType(world, "aa");
        VoxelType b = new TestingVoxelType(world, "b");
        VoxelType c = new TestingVoxelType(world, "c");

        a.place(-1, 2, 6);
        a.place(-1, 2, -4);
        c.place(-1, 2, -5);
        b.place(-1, 2, 2);
        c.place(-1, 2, 0);

        b.place(1, -2, 1);
        c.place(1, -2, -2);

        assertDoesNotThrow(() -> {
            assertIterableEquals(
                List.of(
                    new PlacedVoxel(a, new WorldCoords3d(-1, 2, 6)),
                    new PlacedVoxel(b, new WorldCoords3d(-1, 2, 2)),
                    new PlacedVoxel(c, new WorldCoords3d(-1, 2, 0)),
                    new PlacedVoxel(a, new WorldCoords3d(-1, 2, -4)),
                    new PlacedVoxel(c, new WorldCoords3d(-1, 2, -5))
                ),
                world.voxels(-1, 2)
            );
        });

        assertDoesNotThrow(() -> {
            assertIterableEquals(
                List.of(
                    new PlacedVoxel(b, new WorldCoords3d(1, -2, 1)),
                    new PlacedVoxel(c, new WorldCoords3d(1, -2, -2))
                ),
                world.voxels(1, -2)
            );
        });

        assertDoesNotThrow(() -> {
            assertIterableEquals(
                Collections.emptyList(),
                world.voxels(0, -2)
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
        public VoxelType getVoxel(int x, int y, int z) {
            return null;
        }
    }
}
