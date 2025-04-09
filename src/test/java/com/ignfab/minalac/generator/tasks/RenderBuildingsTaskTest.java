package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class RenderBuildingsTaskTest {
    private WorldBBox3d bbox;
    private TestingVoxelTile tile;
    private Heightmap heightmap;
    private ModelStore models;

    @BeforeEach
    void setUp() {
        bbox = new WorldBBox3d(0, 0, 0, 3, 3, 22);
        tile = new TestingVoxelTile(bbox);
        heightmap = new Heightmap(tile.limits().to2d(), 0);
        models = new ModelStore();
    }

    /**
     * Test the rendering of the building with a model with good specified metadata.
     */
    @Test
    void testBuildingRenderingWithMetadata() {
        WorldBBox2d modelBbox = new WorldBBox2d(0, 0, 3, 3);
        Model building = new TestingRectangleShapeVoxelizable2dModel(modelBbox);

        int expectedHeight = 20;
        building.setMetadata("height", expectedHeight);
        models.add("building", building);

        String voxelAName = "voxelA";
        String voxelBName = "voxelB";
        String voxelCName = "voxelC";
        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection(models, "building", new ModelFilterHasMetadata("height")),
            heightmap,
            new TestingVoxelParams(voxelAName).create(TestingSeed.UNUSED),
            new TestingVoxelParams(voxelBName).create(TestingSeed.UNUSED),
            new TestingVoxelParams(voxelCName).create(TestingSeed.UNUSED)
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
                tile.assertVoxel(voxelAName, c);
            // Air check
            if (z > expectedHeight)
                tile.assertVoxelNull(c);
            // Checks here below the roof of the building
            if (z < expectedHeight) {
                // Windows check
                if (x != 1 && y != 1 && z % 4 == 0)
                    tile.assertVoxel(voxelCName, c);
                // Floors check
                if (x == 1 && y == 1 && z % 4 == 2)
                    tile.assertVoxel(voxelAName, c);
                // Walls check
                if (x != 1 && y != 1 && z % 4 != 0)
                    tile.assertVoxel(voxelBName, c);
                // Air check between two floors
                if (x == 1 && y == 1 && z % 4 != 2)
                    tile.assertVoxelNull(c);
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
        Model building = new TestingRectangleShapeVoxelizable2dModel(modelBbox);
        models.add("building", building);

        Placeable placeable = new TestingVoxelParams("voxel").create(TestingSeed.UNUSED);
        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection(models, "building", new ModelFilterHasMetadata("height")),
            heightmap,
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
        Model building = new TestingRectangleShapeVoxelizable2dModel(modelBbox);

        building.setMetadata("height", -20);
        models.add("building", building);

        Placeable placeable = new TestingVoxelParams("voxel").create(TestingSeed.UNUSED);
        assertDoesNotThrow(() -> new RenderBuildingsTask(
            new ModelSelection(models, "building", new ModelFilterHasMetadata("height")),
            heightmap,
            placeable,
            placeable,
            placeable
        ).run(tile));

        for (WorldCoords3d c : tile.limits())
            tile.assertVoxelNull(c);
    }
}
