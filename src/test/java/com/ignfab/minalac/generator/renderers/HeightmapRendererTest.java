package com.ignfab.minalac.generator.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class HeightmapRendererTest {
    @Test
    public void testRender() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(-1, -2, -1, 3, 3, 3));
        TestingVoxelType voxel = new TestingVoxelType("voxel");

        Heightmap minimum = new Heightmap(-1, -2, 3, 2, 0);
        minimum.set(-1, -1, -1);
        minimum.set(0, -1, -1);
        minimum.set(1, -1, -1);
        /*
          ^
          y
        -1| -1 -1 -1
        -2|  0  0  0
          |---------x>
            -1  0  1
        */

        Heightmap maximum = new Heightmap(-1, -1, 3, 2, 0);
        maximum.set(-1, -1, -3);
        maximum.set(0, -1, 1);
        maximum.set(1, -1, -1);
        /*
          ^
          y
         0|  0  0  0
        -1| -3  1 -1
          |---------x>
            -1  0  1
        */

        HeightmapRenderer renderer = new HeightmapRenderer(minimum, maximum, voxel);
        renderer.render(world);
        // Expected output.
        /*
           ^             ^              ^
           z             z              y
          1|     a       |    a        0|
          0|     a       |    a       -1|    a  a
         -1|     a  a    |    a       -2|
           |---------x>  |---------y>   |---------x>
             -1  0  1     -2 -1  0       -1  0  1
        */

        // Case where minimum = maximum at (x = 1, y = -1).
        // One voxel is placed at (1, -1, -1)
        world.assertVoxel("voxel", 1, -1, -1);
        world.assertVoxelNull(1, -1, 0);
        world.assertVoxelNull(1, -1, 1);

        // Case where minimum < maximum at (x = 0, y = -1).
        // Voxels are placed at (x = 0 , y = -1) from z = -1 (min height) to z = 1 (max height)
        world.assertVoxel("voxel", 0, -1, -1);
        world.assertVoxel("voxel", 0, -1, 0);
        world.assertVoxel("voxel", 0, -1, 1);

        // When minimum > maximum, no voxel is placed
        world.assertVoxelNull(-1, -1, -1);
        world.assertVoxelNull(-1, -1, 0);
        world.assertVoxelNull(-1, -1, 1);

        // Checking that no voxels were placed outside of the intersection of the two heightmaps bbox.
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                world.assertVoxelNull(x, -2, z);
                world.assertVoxelNull(x, 0, z);
            }
    }
}
