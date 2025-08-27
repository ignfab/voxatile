package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RepeatPatternTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RepeatPattern(new PlaceableStructure(), 0, 0, 0, 0, 0, 0));
    }

    @Test
    public void testRepeat() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(0, 0, 0, 9, 9, 1));

        PlaceableStructure structure = new PlaceableStructure();
        structure.set(0, 0, 0, new TestingVoxel("A"));
        structure.set(1, 0, 0, new TestingVoxel("B"));
        structure.set(2, 0, 0, new TestingVoxel("C"));
        RepeatPattern pattern = new RepeatPattern(structure, 0, 0, 0, 0, 0, 0);

        for (int y = 0; y <= tile.limits().maxY(); y++)
            for (int x = 0; x <= tile.limits().maxX(); x++)
                pattern.place(tile, x, y, 0);

        for (int y = 0; y < tile.limits().maxY(); y++)
            for (int nbRepeatition = 0; nbRepeatition < tile.limits().maxX() / structure.limits().maxX() - 1; nbRepeatition++) {
                tile.assertVoxel("A", (nbRepeatition * 3), y, 0);
                tile.assertVoxel("B", (nbRepeatition * 3) + 1, y, 0);
                tile.assertVoxel("C", (nbRepeatition * 3) + 2, y, 0);
            }
    }
}
