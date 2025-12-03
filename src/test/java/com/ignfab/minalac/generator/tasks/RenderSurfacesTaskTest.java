package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingRectangleShape2dModel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

class RenderSurfacesTaskTest {
    private WorldBBox3d bbox;
    private TestingGenerationTile tile;
    private ModelSelection modelSelection;
    private TestingHeightmap heightmap;
    private TestingVoxel voxel;

    @BeforeEach
    public void setUp() {
        bbox = new WorldBBox3d(-1, -2, -3, 4, 5, 6);
        tile = new TestingGenerationTile(bbox);
        heightmap = tile.newStoredHeightmap("altitude", 0);
        modelSelection = new ModelSelection("testing", null);

        voxel = new TestingVoxel("TEST");
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RenderSurfacesTask(modelSelection, heightmap.spec(), voxel));
    }

    @Test
    @DisplayName("Test rendering on a flat heightmap")
    public void testRenderFlatSmaller() {

        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 3, 4);
        tile.models().add("testing", new TestingRectangleShape2dModel(modelBbox));

        new RenderSurfacesTask(modelSelection, heightmap.spec(), voxel).run(tile);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0 && modelBbox.contains(pos.to2d()))
                tile.voxels().assertVoxelNotNull(pos);
            else
                tile.voxels().assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering on a non flat heightmap")
    public void testRenderHeightmap() {

        // Prepare a non flat Heightmap
        for (WorldCoords2d pos : bbox.to2d())
            heightmap.set(pos, (pos.x() + pos.y()) / 2);

        // Add one model covering the whole map
        tile.models().add("testing", new TestingRectangleShape2dModel(bbox.to2d()));

        new RenderSurfacesTask(modelSelection, heightmap.spec(), voxel).run(tile);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == (pos.x() + pos.y()) / 2)
                tile.voxels().assertVoxelNotNull(pos);
            else
                tile.voxels().assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering with a heightmap over world vertical limits")
    public void testRenderVerticalOverflow() {
        // A heightmap overflowing under and over world limits
        for (WorldCoords2d pos : bbox.to2d())
            // At (2, 2), height will be 6, above max world z (i.e. 2)
            // At (-1, -2), height will be -5, under min world z (i.e. -3)
            heightmap.set(pos, pos.x() + pos.y() * 2);

        // Add one model covering the whole map with BORDER voxels
        tile.models().add("testing", new TestingRectangleShape2dModel(bbox.to2d()));

        new RenderSurfacesTask(modelSelection, heightmap.spec(), voxel).run(tile);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == pos.x() + pos.y() * 2)
                tile.voxels().assertVoxelNotNull(pos);
            else
                tile.voxels().assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering of a model larger than world horizontal limits")
    public void testRenderHorizontalOverflow() {
        // Prepare a larger model bbox
        WorldBBox2d modelBbox = new WorldBBox2d(bbox.minX() - 1, bbox.minY() - 1, bbox.sizeX() + 2, bbox.sizeY() + 2);
        tile.models().add("testing", new TestingRectangleShape2dModel(modelBbox));

        new RenderSurfacesTask(modelSelection, heightmap.spec(), voxel).run(tile);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0)
                tile.voxels().assertVoxelNotNull(pos);
            else
                tile.voxels().assertVoxelNull(pos);
        }
    }
}
