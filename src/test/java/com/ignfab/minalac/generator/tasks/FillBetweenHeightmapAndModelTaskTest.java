package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.testing.TestingVoxel;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class FillBetweenHeightmapAndModelTaskTest {
    @Test
    void testFlattenSurface() {
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(0, 0, 0, 10, 10, 10));
        TestingHeightmap heightmap = tile.newStoredHeightmap("heightmap", 0);

        // Prepare a diagonal heightmap
        for (WorldCoords2d pos : heightmap.bbox())
            heightmap.set(pos, pos.x());

        // Prepare a single square model that has the same size as the tile.
        ShapesVoxelizable2d model = new TestingRectangleShapeVoxelizable2dModel(tile.limits().to2d());
        int zMetadata = 5;
        model.setMetadata("zTest", zMetadata);
        tile.models().add("model", model);

        // Run leveling
        assertDoesNotThrow(() -> new FillBetweenHeightmapAndMetadataTask(
            new ModelSelection("model", null),
            heightmap.spec(),
            "zTest",
            new TestingVoxel("A"),
            new TestingVoxel("B")
        ).run(tile));

        // Verify filling
        for (WorldCoords3d c : tile.limits()) {
            int zHeightmap = heightmap.get(c.to2d());

            if (zHeightmap <= c.z() && c.z() <= zMetadata)
                tile.voxels().assertVoxel("B", c);
            if (c.z() > zMetadata && zHeightmap >= c.z())
                tile.voxels().assertVoxel("A", c);
        }
    }
}
