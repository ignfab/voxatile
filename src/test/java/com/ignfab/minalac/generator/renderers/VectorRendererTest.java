package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingVoxelizable2dBoxModel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.SemanticType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class VectorRendererTest {
    private TestingVoxelWorld world;
    private Heightmap heightmap;
    private WorldBBox3d bbox;
    private LinkedList<Model> models;

    @BeforeEach
    public void setUp() {
        bbox = new WorldBBox3d(-1, -2, -3, 4, 5, 6);
        world = new TestingVoxelWorld(bbox);
        heightmap = new Heightmap(bbox.to2d(), -1);
        models = new LinkedList<>();
    }

    private void render() {
        assertDoesNotThrow(
            () -> new VectorRenderer(
                    heightmap,
                    models,
                    world.getFactory().createVoxelType(SemanticType.COBBLE),
                    world.getFactory().createVoxelType(SemanticType.BRICK)
                ).render(bbox)
        );
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new VectorRenderer(
            heightmap,
            models,
            world.getFactory().createVoxelType(SemanticType.COBBLE),
            world.getFactory().createVoxelType(SemanticType.BRICK)
        ));
    }


    private final class TestNotVoxelizableModel extends Model {}

    @Test
    @DisplayName("Test a simple rendering on flat heightmap with no inside")
    public void testRenderFlat() {

        // This box is too small to have inside voxels
        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 2, 3);

        // Add one model covering the whole map with INSIDE voxels
        models.add(new TestingVoxelizable2dBoxModel(modelBbox));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0 && modelBbox.contains(pos.to2d()))
                world.assertVoxel("brick", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering inside and border")
    public void testRenderInsideBorder() {

        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 3, 4);
        models.add(new TestingVoxelizable2dBoxModel(modelBbox));

        render();

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
                world.assertVoxel("brick", pos);
            else
                // Inside
                world.assertVoxel("cobble", pos);
        }
    }

    @Test
    @DisplayName("Test rendering on a non flat heightmap")
    public void testRenderHeightmap() {
        // Prepare a non flat Heightmap
        for (WorldCoords2d pos : bbox.to2d())
            heightmap.set(pos, (pos.x() + pos.y()) / 2);

        // Add one model covering the whole map
        models.add(new TestingVoxelizable2dBoxModel(bbox.to2d()));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == (pos.x() + pos.y()) / 2 + 1) // + 1 because vector renderer places voxels 1 pos above heightmap
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
        models.add(new TestingVoxelizable2dBoxModel(bbox.to2d()));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == pos.x() + pos.y() * 2 + 1)
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
        System.out.println(modelBbox);
        models.add(new TestingVoxelizable2dBoxModel(modelBbox));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0)
                world.assertVoxel("cobble", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering of a non voxelizable model")
    public void testRenderNonVoxelizable() {
        models.add(new TestNotVoxelizableModel());

        render();
    }
}
