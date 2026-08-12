package com.ignfab.minalac.generator.world;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class VoxelWorldTest {
    @Test
    public void testSetLimits() {
        VoxelWorld world = new VoxelWorldMock();
        assertThrows(IllegalArgumentException.class, () -> world.setLimits(new WorldBBox3d(
            new WorldCoords3d(-21, -19, -18),
            new WorldCoords3d(21, 22, 23)
        )));

        assertDoesNotThrow(() -> world.setLimits(new WorldBBox3d(
            new WorldCoords3d(-1, -2, -3),
            new WorldCoords3d(4, 5, 6))
        ));

        assertEquals(new WorldBBox3d(new WorldCoords3d(-1, -2, -3), new WorldCoords3d(4, 5, 6)), world.limits());
        assertEquals(new WorldCoords3d(2, 2, 2), world.getMetadata().getSpawn());

        assertThrows(IllegalStateException.class, () -> world.setLimits(new WorldBBox3d(
            new WorldCoords3d(-1, -2, -3),
            new WorldCoords3d(7, 8, 9))
        ));
    }

    @Test
    public void testVoxelIterator() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(new WorldCoords3d(-1, -2, -5), new WorldCoords3d(2, 3, 6)));
        Placeable a = new TestingVoxel("aa");
        Placeable b = new TestingVoxel("b");
        Placeable c = new TestingVoxel("c");

        a.place(tile, -1, 2, 6);
        a.place(tile, -1, 2, -4);
        c.place(tile, -1, 2, -5);
        b.place(tile, -1, 2, 2);
        c.place(tile, -1, 2, 0);

        b.place(tile, 1, -2, 1);
        c.place(tile, 1, -2, -2);

        assertDoesNotThrow(() -> assertIterableEquals(
            List.of(
                new PlacedVoxel(a, new WorldCoords3d(-1, 2, 6)),
                new PlacedVoxel(b, new WorldCoords3d(-1, 2, 2)),
                new PlacedVoxel(c, new WorldCoords3d(-1, 2, 0)),
                new PlacedVoxel(a, new WorldCoords3d(-1, 2, -4)),
                new PlacedVoxel(c, new WorldCoords3d(-1, 2, -5))
            ),
            tile.voxels(-1, 2)
        ));

        assertDoesNotThrow(() -> assertIterableEquals(
            List.of(
                new PlacedVoxel(b, new WorldCoords3d(1, -2, 1)),
                new PlacedVoxel(c, new WorldCoords3d(1, -2, -2))
            ),
            tile.voxels(1, -2)
        ));

        assertDoesNotThrow(() -> assertIterableEquals(
            Collections.emptyList(),
            tile.voxels(0, -2)
        ));
    }

    private static class VoxelWorldMock extends VoxelWorld {

        protected VoxelWorldMock() {
            super(new VoxelWorldMetadata());
        }

        @Override
        public WorldBBox3d maxLimits() {
            return new WorldBBox3d(
                new WorldCoords3d(-20, -19, -18),
                new WorldCoords3d(21, 22, 23)
            );
        }

        @Override
        public void initialize() {}

        @Override
        public void finalizeAndSave() {}

        @Override
        public VoxelTile newTile(WorldBBox3d limits) {
            throw new UnsupportedOperationException("Unimplemented method 'newTile'");
        }

        @Override
        public Collection<WorldBBox2d> tiles(int maxTileSize) {
            return Collections.singleton(maxLimits().to2d());
        }
    }
}
