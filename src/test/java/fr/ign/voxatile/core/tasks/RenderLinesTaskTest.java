package fr.ign.voxatile.core.tasks;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.computed.ConstantHeightmap;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.TestingShape3dModel;
import fr.ign.voxatile.core.placeables.PlaceableStructure;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.voxelization.shape3d.LineString3d;
import fr.ign.voxatile.core.world.TestingVoxel;

import static org.junit.jupiter.api.Assertions.*;

/*
 Here we try not to test voxelization but only rendering.
 This means that we won't test correct voxelization results but only that rendering is ok according to parameters.
*/
class RenderLinesTaskTest {
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
        assertDoesNotThrow(() -> new RenderLinesTask(modelSelection, PlaceableStructure.EMPTY, new ConstantHeightmap(0)));
        assertDoesNotThrow(() -> new RenderLinesTask(modelSelection, PlaceableStructure.EMPTY, null));
    }

    @Test
    public void testRenderWithoutHeightmap() {

        tile.models().add("testing", List.of(new TestingShape3dModel(LineString3d.fromPoints(bbox.min(), bbox.max()))));
        PlaceableStructure structure = PlaceableStructure.builder()
            .set(new WorldCoords3d(0, 0, 0), new TestingVoxel("TEST"))
            .build();

        // Test rendering works
        assertDoesNotThrow(() -> new RenderLinesTask(modelSelection, structure, null).run(tile), "Render should not throw");

        // Test some voxels are rendered
        assertDoesNotThrow(() -> {
            for (WorldCoords3d pos : bbox)
                if (tile.voxels().get(pos) != null)
                    return; // Test is ok, we found a voxel !
            throw new Exception("No voxel rendered");
        }, "Voxel expected to be rendered");

        // Test all voxels have not the same altitude
        int z = bbox.minZ(); // We know we have starting point at this altitude.
        assertDoesNotThrow(() -> {
            for (WorldCoords3d pos : bbox)
                if (pos.z() != z && tile.voxels().get(pos) != null)
                    return; // Test is ok, we found a voxel at another altitude !
            throw new Exception("All voxels have the same altitude");
        }, "Rendered voxels expected to have different altitudes");
    }

    @Test
    public void testRenderWithHeightmap() {

        tile.models().add("testing", List.of(new TestingShape3dModel(LineString3d.fromPoints(bbox.min(), bbox.max()))));
        PlaceableStructure structure = PlaceableStructure.builder()
            .set(new WorldCoords3d(0, 0, 0), new TestingVoxel("TEST"))
            .build();

        // Test rendering works
        assertDoesNotThrow(() -> new RenderLinesTask(modelSelection, structure, new ConstantHeightmap(-1)).run(tile), "Render should not throw");

        // Test some voxels are rendered
        assertDoesNotThrow(() -> {
            for (WorldCoords3d pos : bbox)
                if (tile.voxels().get(pos) != null)
                    return; // Test is ok, we found a voxel !
            throw new Exception("No voxel rendered");
        }, "Voxel expected to be rendered");

        // Test all rendered voxels are over or on heightmap ("render only above")
        for (WorldCoords3d pos : bbox)
            if (tile.voxels().get(pos) != null)
                assertTrue(pos.z() >= -1, "Voxel at %s expected to be over or on heightmap".formatted(pos));
    }
}
