package com.ignfab.minalac.generator.world;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.Test;

public class PlaceableStructureTest {

    @Test
    public void testPlace() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(-1, -2, -3, 3, 3, 3));


        VoxelType vtA = new TestingVoxelType(world, "A");
        VoxelType vtB = new TestingVoxelType(world, "B");
        VoxelType vtC = new TestingVoxelType(world, "C");
        VoxelType vtD = new TestingVoxelType(world, "D");
        VoxelType vtE = new TestingVoxelType(world, "E");

        // World voxel configuration
        /* z :    -2   -1
                +---+ +---+
        y       |AAA| |  A|
        ^       |AAA| |   |
        |       |AAA| |  A|
        + - > x +---+ +---+ */
        for (int x = -1; x <= 1; x++)
            for (int y = -2; y <= 0; y++)
                vtA.place(x, y, -2); // layer of A at z = -2
        vtA.place(1, 0, -1); // |  A| at z = -1 & y = 0
        vtA.place(1, -2, -1); // |  A| at z = -1 & y = -2

        // Structure voxel configuration
        /* z:     -1    0     1
                +---+ +---+ +---+
        y       |BBB| | E | |B  |
        ^       |B B| |CDE| |   |
        |       |  B| | C | |   |
        + - > x +---+ +---+ +---+ */
        PlaceableStructure structure = new PlaceableStructure();

        // z = -1 : fill with B
        structure.set(new WorldBBox3d(-1, -1, -1, 3, 3, 1), vtB);
        // z = -1 : remove the voxel at the middle -> |B B|
        structure.set(0, 0, -1, null);
        // z = -1 : remove the two voxels -> |  B|
        structure.remove(new WorldBBox3d(-1, -1, -1, 2, 1, 1));

        // z = 0 | E |
        structure.set(0, 1, 0, vtE);
        // z = 0 |CDE|
        structure.set(-1, 0, 0, vtC);
        structure.set(0, 0, 0, vtD);
        structure.set(1, 0, 0, vtE);
        // z = 0 | C |
        structure.set(0, -1, 0, vtC);

        // z = 1 |B  |
        structure.set(-1, 1, 1, vtB);

        // Structure is placed at the center of the world
        structure.place(0, -1, -2);

        // Expected outcome
        /* z:     -3   -2    -1
                +---+ +---+ +---+
        y       |BBB| |AEA| |B A|
        ^       |B B| |CDE| |   |
        |       |  B| |ACA| |  A|
        + - > x +---+ +---+ +---+ */

        // z = -3
        // y = 0 : |BBB|
        world.assertVoxel("B", -1, 0, -3);
        world.assertVoxel("B", 0, 0, -3);
        world.assertVoxel("B", 1, 0, -3);

        // y = -1 : |B B|
        world.assertVoxel("B", -1, -1, -3);
        world.assertVoxelNull(0, -1, -3);
        world.assertVoxel("B", 1, -1, -3);

        // y = -2 : |  B|
        world.assertVoxelNull(-1, -2, -3);
        world.assertVoxelNull(0, -2, -3);
        world.assertVoxel("B", 1, -2, -3);

        // z = -2
        // y = 0 : |AEA|
        world.assertVoxel("A", -1, 0, -2);
        world.assertVoxel("E", 0, 0, -2);
        world.assertVoxel("A", 1, 0, -2);

        // y = -1 : |CDE|
        world.assertVoxel("C", -1, -1, -2);
        world.assertVoxel("D", 0, -1, -2);
        world.assertVoxel("E", 1, -1, -2);

        // y = -2 : |ACA|
        world.assertVoxel("A", -1, -2, -2);
        world.assertVoxel("C", 0, -2, -2);
        world.assertVoxel("A", 1, -2, -2);

        // z = -1
        // y = 0 : |B A|
        world.assertVoxel("B", -1, 0, -1);
        world.assertVoxelNull(0, 0, -1);
        world.assertVoxel("A", 1, 0, -1);

        // y = -1 : |   |
        world.assertVoxelNull(-1, -1, -1);
        world.assertVoxelNull(0, -1, -1);
        world.assertVoxelNull(1, -1, -1);

        // y = -2 : |  A|
        world.assertVoxelNull(-1, -2, -1);
        world.assertVoxelNull(0, -2, -1);
        world.assertVoxel("A", 1, -2, -1);
    }

    @Test
    public void testPlaceWithEmptyStructure() {
        TestingVoxelWorld world = new TestingVoxelWorld(new WorldBBox3d(3, 4, 5, 2, 1, 1));

        VoxelType vt = new TestingVoxelType(world, "*");
        vt.place(4, 4, 5);

        PlaceableStructure structure = new PlaceableStructure();
        structure.place(3, 4, 5);
        structure.place(4, 4, 5);

        world.assertVoxelNull(3, 4, 5);
        world.assertVoxel("*", 4, 4, 5);
    }
}
