package com.ignfab.minalac.generator.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.models.TestingLineShapeVoxelizable3dModel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.IntegerIntervals;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class LinearRendererTest {

    final String PLACED = "LINE";

    /**
     * Counts all voxels at a 2d position and for all z.
     *
     * @param pos 2d position to test
     * @param world world where voxels are
     */
    private int countAt2dPosition(WorldCoords2d pos, TestingVoxelWorld world) {
        if (!world.limits().to2d().contains(pos))
            return 0;
        int maxZ = world.limits().max().z();
        int count = 0;
        for (int z = world.limits().min().z(); z <= maxZ; z++)
            if (world.get(pos.x(), pos.y(), z) == PLACED)
                count++;
        return count;
    }

    /**
     * Count existing voxel connected to a given position if they were
     * projected on a 2d plane. Voxels are connected if they share (on
     * 2d plane) one of their sides.
     * This is involved in connectedness test.
     *
     * @param pos position to test
     * @param world world where voxels are
     * @return number of connected voxels
     */
    private int countConnected2d(WorldCoords2d pos, TestingVoxelWorld world) {
        return
            countAt2dPosition(new WorldCoords2d(pos.x() + 1, pos.y()), world) +
            countAt2dPosition(new WorldCoords2d(pos.x() - 1, pos.y()), world) +
            countAt2dPosition(new WorldCoords2d(pos.x(), pos.y() + 1), world) +
            countAt2dPosition(new WorldCoords2d(pos.x(), pos.y()- 1), world);
    }

    /**
     * Here, we test that a 1 voxel line has all its voxels connected (on 2d plane).
     */
    private void testConnectednessLine(WorldCoords3d start, WorldCoords3d end) {
        IntegerIntervals at = new IntegerIntervals();
        at.add(0, 0);
        WorldBBox3d bbox = new WorldBBox3d(
            start.x() - 1, start.y() - 1, start.z() - 1,
            end.x() - start.x() + 2, end.y() - start.y() + 2, end.z() - start.z() + 2
        );

        ModelStore store = new ModelStore();
        TestingVoxelWorld world = new TestingVoxelWorld(bbox);
        ModelSelection selection = new ModelSelection(store, "testing", null);
        LinearRenderer renderer = null;// new LinearRenderer(selection, new TestingVoxelType(world, PLACED), at, null);

        store.add("testing", new TestingLineShapeVoxelizable3dModel(start, end));
        renderer.render(bbox);

        for (WorldCoords2d pos : world.limits().to2d())
            if (countAt2dPosition(pos, world) > 0)
                if (pos.equals(start.to2d()) || pos.equals(end.to2d()))
                    assertEquals(1, countConnected2d(pos, world), "From %s to %s, at %s".formatted(start, end, pos));
                else
                    assertEquals(2, countConnected2d(pos, world), "From %s to %s, at %s".formatted(start, end, pos));
    }

    @Test
    void testConnectedness() {
        testConnectednessLine(new WorldCoords3d(1, 1, 0), new WorldCoords3d(8, 8, 0));
        testConnectednessLine(new WorldCoords3d(1, 1, 0), new WorldCoords3d(1, 8, 0));
        testConnectednessLine(new WorldCoords3d(1, 1, 0), new WorldCoords3d(3, 8, 0));
        testConnectednessLine(new WorldCoords3d(8, 8, 0), new WorldCoords3d(1, 1, 0));
        testConnectednessLine(new WorldCoords3d(1, 8, 0), new WorldCoords3d(1, 1, 0));
        testConnectednessLine(new WorldCoords3d(3, 8, 0), new WorldCoords3d(1, 1, 0));
    }

    // TESTER L'ABSENCE DE TROUS!
}