package com.ignfab.minalac.generator.tasks;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingRectangleShape2dModel;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class RenderBuildingsTaskTest {
    private TestingGenerationTile tile;

    @BeforeEach
    void setUp() {
        WorldBBox3d bbox = new WorldBBox3d(0, 0, 0, 3, 3, 22);
        tile = new TestingGenerationTile(bbox);
    }

    /**
     * Test the rendering of the building with a model with good specified metadata.
     */
    @Test
    void testBuildingRenderingWithMetadata() {
        WorldBBox2d modelBbox = new WorldBBox2d(0, 0, 3, 3);
        Model building = new TestingRectangleShape2dModel(modelBbox);

        int expectedHeight = 20;
        // Required metadata
        building.setMetadata("height", expectedHeight);
        building.setMetadata("minimum-ground-altitude", 0);
        building.setMetadata("ground-floor-altitude", 0);
        tile.models().add("building", List.of(building));

        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection("building", new ModelFilterHasMetadata("height")),
            new TestingVoxel("roof"),
            new TestingVoxel("wall"),
            new TestingVoxel("window")
        ).run(tile));

        for (WorldCoords3d c : tile.limits()) {
            int x = c.x();
            int y = c.y();
            int z = c.z();

            // The building is always above the terrain.
            // In this test, the (flat) ground is at z = 0,
            // thus the building starts at z = 1
            if (z == 0)
                continue;

            // Roof check
            if (z == expectedHeight)
                tile.voxels().assertVoxel("roof", c);
            // Air check
            if (z > expectedHeight)
                tile.voxels().assertVoxelNull(c);
            // Checks here below the roof of the building
            if (z < expectedHeight) {
                // Windows check
                if (x != 1 && y != 1 && z % 4 == 0)
                    tile.voxels().assertVoxel("window", c);
                // Walls check
                if (x != 1 && y != 1 && z % 4 != 0)
                    tile.voxels().assertVoxel("wall", c);
                // Air check between two floors
                if (x == 1 && y == 1 && z % 4 != 2)
                    tile.voxels().assertVoxelNull(c);
            }
        }
    }

    /**
     * Test the rendering of the building with a model that
     * does not have 'height' metadata.
     */
    @Test
    void testBuildingRenderingWithoutMetadata() {
        WorldBBox2d modelBbox = new WorldBBox2d(0, 0, 3, 3);
        Model building = new TestingRectangleShape2dModel(modelBbox);
        tile.models().add("building", List.of(building));

        Placeable placeable = new TestingVoxelParams("voxel").create(TestingSeed.UNUSED);
        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection("building", new ModelFilterHasMetadata("height")),
            placeable,
            placeable,
            placeable
        ).run(tile));
    }

    /**
     * Test the rendering of the building with a model
     * that contains negative 'height' metadata.
     */
    @Test
    void testBuildingRenderingWithNegativeHeight() {
        WorldBBox2d modelBbox = new WorldBBox2d(0, 0, 3, 3);
        Model building = new TestingRectangleShape2dModel(modelBbox);

        building.setMetadata("height", -20);
        tile.models().add("building", List.of(building));

        Placeable placeable = new TestingVoxelParams("voxel").create(TestingSeed.UNUSED);
        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection("building", new ModelFilterHasMetadata("height")),
            placeable,
            placeable,
            placeable
        ).run(tile));

        for (WorldCoords3d c : tile.limits())
            tile.voxels().assertVoxelNull(c);
    }
}
