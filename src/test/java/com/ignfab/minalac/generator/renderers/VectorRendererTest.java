package com.ignfab.minalac.generator.renderers;
/*
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.GeometryModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingRasterizableModel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.SemanticType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class VectorRendererTest {
    private TestingVoxelWorld world;
    private Heightmap heightMap;
    private WorldBBox3d bbox;
    private LinkedList<Model> models;

    @BeforeEach
    public void setUp() {
        bbox = new WorldBBox3d(-1, -2, -3, 4, 5, 6);
        world = new TestingVoxelWorld(bbox);
        heightMap = new Heightmap(bbox.to2d(), -1);
        models = new LinkedList<>();
    }

    private void render() {
        assertDoesNotThrow(
            () -> new VectorRenderer(
                    heightMap,
                    models,
                    world.getFactory().createVoxelType(SemanticType.COBBLE),
                    world.getFactory().createVoxelType(SemanticType.BRICK)
                ).render()
        );
    }

    @Test
    @DisplayName("Test a simple rendering on flat height map")
    public void testRenderFlat() throws OutOfWorldException {
        // Prepare a chunk smaller than the whole world
        WorldBBox2d modelBbox = new WorldBBox2d(0, -1, 2, 3);

        // Add one model covering the whole map with INSIDE voxels
        models.add(new TestingRasterizableModel(new TestingIterableArrayChunk2d(modelBbox, GeometryModel.INSIDE)));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0 && modelBbox.contains(pos.to2d()))
                world.assertVoxel("cobble", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering outside, inside and border")
    public void testRenderOutsideInsideBorder() throws OutOfWorldException {
        // Prepare a chunk with only three pixels, one per available value
        TestingIterableArrayChunk2d chunk = new TestingIterableArrayChunk2d(new WorldBBox2d(0, 0, 1, 3), GeometryModel.OUTSIDE);
        chunk.set(0, 1, GeometryModel.BORDER);
        chunk.set(0, 2, GeometryModel.INSIDE);

        // Add one model covering the whole map with INSIDE voxels
        models.add(new TestingRasterizableModel(chunk));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0 && pos.x() == 0 && pos.y() == 1)
                world.assertVoxel("brick", pos);
            else if (pos.z() == 0 && pos.x() == 0 && pos.y() == 2)
                world.assertVoxel("cobble", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering on a non flat height map")
    public void testRenderHeightMap() throws OutOfWorldException {
        // Prepare a non flat HeightMap
        for (WorldCoords2d pos : bbox.to2d())
            heightMap.set(pos, (pos.x() + pos.y()) / 2);

        // Add one model covering the whole map with BORDER voxels
        models.add(new TestingRasterizableModel(new TestingIterableArrayChunk2d(bbox.to2d(), GeometryModel.BORDER)));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == (pos.x() + pos.y()) / 2 + 1) // + 1 because vector renderer places voxels 1 pos above height map
                world.assertVoxel("brick", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering with a height map over world vertical limits")
    public void testRenderVerticalOverflow() throws OutOfWorldException {
        // A heightmap overflowing under and over world limits
        for (WorldCoords2d pos : bbox.to2d())
            // At (2, 2), height will be 6, above max world z (i.e. 2)
            // At (-1, -2), height will be -5, under min world z (i.e. -3)
            heightMap.set(pos, pos.x() + pos.y() * 2);

        // Add one model covering the whole map with BORDER voxels
        models.add(new TestingRasterizableModel(new TestingIterableArrayChunk2d(bbox.to2d(), GeometryModel.BORDER)));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == pos.x() + pos.y() * 2 + 1)
                world.assertVoxel("brick", pos);
            else
                world.assertVoxelNull(pos);
        }
    }

    @Test
    @DisplayName("Test rendering of a chunk larger than world horizontal limits")
    public void testRenderHorizontalOverflow() throws OutOfWorldException {
        // Prepare a larger model bbox
        WorldBBox2d modelBbox = new WorldBBox2d(bbox.getMinX() - 1, bbox.getMinY() - 1, bbox.getSizeX() + 2, bbox.getSizeY() + 2);

        models.add(new TestingRasterizableModel(new TestingIterableArrayChunk2d(modelBbox, GeometryModel.INSIDE)));

        render();

        for (WorldCoords3d pos : bbox) {
            if (pos.z() == 0)
                world.assertVoxel("cobble", pos);
            else
                world.assertVoxelNull(pos);
        }
    }
}
*/
