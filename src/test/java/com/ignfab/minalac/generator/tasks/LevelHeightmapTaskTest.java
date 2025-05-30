package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class LevelHeightmapTaskTest {
    private WorldBBox3d bbox;

    @BeforeEach
    void setUp() {
        bbox = new WorldBBox3d(-1, -2, -20, 7, 6, 40);
    }

    /**
     * Non-flat world simulation.
     *
     * @param pos horizontal position in the voxel world.
     * @return an arbitrary height
     */
    private int heightFormula(WorldCoords2d pos) {
        return (int) Math.floor(pos.x() * .3 + pos.y() * .7);
    }

    @Test
    void testLevelingRendering() {
        TestingGenerationTile tile = new TestingGenerationTile(bbox);
        TestingHeightmap ground = tile.newStoredHeightmap("ground", 0);
        TestingVoxelParams placeable = new TestingVoxelParams("voxel");

        // Prepare a non-flat Heightmap
        for (WorldCoords2d pos : ground.bbox())
            ground.set(pos, heightFormula(pos));

        // Prepare a single square model
        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 5, 3);
        int expectedHeight = 1;
        Model model = new TestingRectangleShapeVoxelizable2dModel(modelBbox);
        tile.models().add("model", model);

        // Try rendering
        assertDoesNotThrow(() -> new LevelGroundTask(
            new ModelSelection("model", null),
            ground.spec(),
            placeable.create(TestingSeed.UNUSED)
        ).run(tile));

        // Verify Heightmap has been updated only where wanted
        for (WorldCoords2d pos : tile.limits().to2d()) {
            if (modelBbox.contains(pos)) {
                // According to formula, max is at (4, 1) (bottom right corner of model) and is 1
                assertEquals(expectedHeight, ground.get(pos), "%s: unexpected value".formatted(pos));
            } else {
                // The rest of the map should not be changed
                assertEquals(heightFormula(pos), ground.get(pos), "%s: unexpected value".formatted(pos));
            }
        }

        // Verify world contents
        for (WorldCoords3d pos : tile.limits()) {
            // Renderers fills from ground (included) to leveled height (1 here)
            if (modelBbox.contains(pos.to2d()) && pos.z() >= heightFormula(pos.to2d()) && pos.z() <= expectedHeight) {
                tile.voxels().assertVoxel("voxel", pos);
            } else {
                tile.voxels().assertVoxelNull(pos);
            }
        }
    }
}
