package com.ignfab.minalac.generator.tasks;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.computed.ConstantHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingShape2dModel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.world.TestingVoxel;

import static org.junit.jupiter.api.Assertions.*;

/*
 Here we try not to test voxelization but only rendering.
 This means that we won't test correct voxelization results but only that rendering is ok according to parameters.
*/
class RenderLines2dTaskTest {
    private WorldBBox3d bbox;
    private TestingGenerationTile tile;
    private ModelSelection modelSelection;

    @BeforeEach
    public void setUp() {
        // bbox must be larger than a single voxel in each dimension for tests to work
        bbox = new WorldBBox3d(-1, -2, -3, 4, 5, 6);
        tile = new TestingGenerationTile(bbox);
        modelSelection = new ModelSelection("testing", null);
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RenderLines2dTask(modelSelection, PlaceableStructure.EMPTY, new ConstantHeightmap(0)));
    }

    @Test
    public void testRender() {

        tile.models().add("testing", List.of(new TestingShape2dModel(LineString2d.fromPoints(bbox.min().to2d(), bbox.max().to2d()))));
        PlaceableStructure structure = PlaceableStructure.builder()
            .set(new WorldCoords3d(0, 0, 0), new TestingVoxel("TEST"))
            .build();

        // Test rendering works
        assertDoesNotThrow(() -> new RenderLines2dTask(modelSelection, structure, new ConstantHeightmap(-1)).run(tile), "Render should not throw");

        // Test some voxels are rendered
        assertDoesNotThrow(() -> {
            for (WorldCoords3d pos : bbox)
                if (tile.voxels().get(pos) != null)
                    return; // Test is ok, we found a voxel!
            throw new Exception("No voxel rendered");
        }, "Voxel expected to be rendered");

        // Test all rendered voxels are on heightmap
        for (WorldCoords3d pos : bbox)
            if (tile.voxels().get(pos) != null)
                assertEquals(-1, pos.z(), "Voxel at %s expected to be on heightmap".formatted(pos));
    }
}
