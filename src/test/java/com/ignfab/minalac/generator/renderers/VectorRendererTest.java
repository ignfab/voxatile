package com.ignfab.minalac.generator.renderers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

class VectorRendererTest {
    private TestingVoxelWorld world;
    private Heightmap heightmap;
    private WorldBBox3d bbox;
    private ModelStore store;
    private ModelSelection modelSelection;
    private VoxelType inside;
    private VoxelType edge;

    @BeforeEach
    public void setUp() {
        bbox = new WorldBBox3d(-1, -2, -3, 4, 5, 6);
        world = new TestingVoxelWorld(bbox);
        heightmap = new Heightmap(bbox.to2d(), 0);
        store = new ModelStore();
        modelSelection = new ModelSelection(store, "testing", null);

        inside = new TestingVoxelType("INSIDE");
        edge = new TestingVoxelType("EDGE");
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new VectorRenderer(modelSelection, heightmap, inside, edge));
    }

    @Test
    @DisplayName("Test a simple rendering on flat heightmap with no inside")
    public void testRenderFlat() {

        // This box is too small to have inside voxels
        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 2, 3);

        // Add one model covering the whole map with INSIDE voxels
        store.add("testing", new TestingRectangleShapeVoxelizable2dModel(modelBbox));

        new VectorRenderer(modelSelection, heightmap, inside, edge).render(world);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0 && modelBbox.contains(pos.to2d()))
                world.assertVoxel("EDGE", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering inside and border")
    public void testRenderInsideBorder() {

        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 3, 4);
        store.add("testing", new TestingRectangleShapeVoxelizable2dModel(modelBbox));

        new VectorRenderer(modelSelection, heightmap, inside, edge).render(world);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() != 0 || !modelBbox.contains(pos.to2d())) {
                // Outside
                world.assertVoxelNull(pos);
                continue;
            }

            if (pos.x() == modelBbox.minX()
                || pos.x() == modelBbox.maxX()
                || pos.y() == modelBbox.minY()
                || pos.y() == modelBbox.maxY())
                // Border
                world.assertVoxel("EDGE", pos);
            else
                // Inside
                world.assertVoxel("INSIDE", pos);
        }
    }

    @Test
    @DisplayName("Test rendering on a non flat heightmap")
    public void testRenderHeightmap() {

        // Prepare a non flat Heightmap
        for (WorldCoords2d pos : bbox.to2d())
            heightmap.set(pos, (pos.x() + pos.y()) / 2);

        // Add one model covering the whole map
        store.add("testing", new TestingRectangleShapeVoxelizable2dModel(bbox.to2d()));

        new VectorRenderer(modelSelection, heightmap, inside, edge).render(world);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == (pos.x() + pos.y()) / 2)
                world.assertVoxelNotNull(pos);
            else
                world.assertVoxelNull(pos);
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
        store.add("testing", new TestingRectangleShapeVoxelizable2dModel(bbox.to2d()));

        new VectorRenderer(modelSelection, heightmap, inside, edge).render(world);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == pos.x() + pos.y() * 2)
                world.assertVoxelNotNull(pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering of a model larger than world horizontal limits")
    public void testRenderHorizontalOverflow() {
        // Prepare a larger model bbox
        WorldBBox2d modelBbox = new WorldBBox2d(bbox.minX() - 1, bbox.minY() - 1, bbox.sizeX() + 2, bbox.sizeY() + 2);
        store.add("testing", new TestingRectangleShapeVoxelizable2dModel(modelBbox));

        new VectorRenderer(modelSelection, heightmap, inside, edge).render(world);

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0)
                world.assertVoxel("INSIDE", pos);
            else
                world.assertVoxelNull(pos);
        }
    }
}
