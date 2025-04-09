package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceableStructureTest {

    @Test
    public void testPlace() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(-1, -2, -3, 3, 3, 3));

        TestingVoxel vtA = new TestingVoxel("A");
        TestingVoxel vtB = new TestingVoxel("B");
        TestingVoxel vtC = new TestingVoxel("C");
        TestingVoxel vtD = new TestingVoxel("D");
        TestingVoxel vtE = new TestingVoxel("E");

        // Voxel configuration
        /* z :    -2   -1
                +---+ +---+
        y       |AAA| |  A|
        ^       |AAA| |   |
        |       |AAA| |  A|
        + - > x +---+ +---+ */
        for (int x = -1; x <= 1; x++)
            for (int y = -2; y <= 0; y++)
                vtA.place(tile, x, y, -2); // layer of A at z = -2
        vtA.place(tile, 1, 0, -1); // |  A| at z = -1 & y = 0
        vtA.place(tile, 1, -2, -1); // |  A| at z = -1 & y = -2

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
        structure.place(tile, 0, -1, -2);

        // Expected outcome
        /* z:     -3   -2    -1
                +---+ +---+ +---+
        y       |BBB| |AEA| |B A|
        ^       |B B| |CDE| |   |
        |       |  B| |ACA| |  A|
        + - > x +---+ +---+ +---+ */

        // z = -3
        // y = 0 : |BBB|
        tile.assertVoxel("B", -1, 0, -3);
        tile.assertVoxel("B", 0, 0, -3);
        tile.assertVoxel("B", 1, 0, -3);

        // y = -1 : |B B|
        tile.assertVoxel("B", -1, -1, -3);
        tile.assertVoxelNull(0, -1, -3);
        tile.assertVoxel("B", 1, -1, -3);

        // y = -2 : |  B|
        tile.assertVoxelNull(-1, -2, -3);
        tile.assertVoxelNull(0, -2, -3);
        tile.assertVoxel("B", 1, -2, -3);

        // z = -2
        // y = 0 : |AEA|
        tile.assertVoxel("A", -1, 0, -2);
        tile.assertVoxel("E", 0, 0, -2);
        tile.assertVoxel("A", 1, 0, -2);

        // y = -1 : |CDE|
        tile.assertVoxel("C", -1, -1, -2);
        tile.assertVoxel("D", 0, -1, -2);
        tile.assertVoxel("E", 1, -1, -2);

        // y = -2 : |ACA|
        tile.assertVoxel("A", -1, -2, -2);
        tile.assertVoxel("C", 0, -2, -2);
        tile.assertVoxel("A", 1, -2, -2);

        // z = -1
        // y = 0 : |B A|
        tile.assertVoxel("B", -1, 0, -1);
        tile.assertVoxelNull(0, 0, -1);
        tile.assertVoxel("A", 1, 0, -1);

        // y = -1 : |   |
        tile.assertVoxelNull(-1, -1, -1);
        tile.assertVoxelNull(0, -1, -1);
        tile.assertVoxelNull(1, -1, -1);

        // y = -2 : |  A|
        tile.assertVoxelNull(-1, -2, -1);
        tile.assertVoxelNull(0, -2, -1);
        tile.assertVoxel("A", 1, -2, -1);
    }

    @Test
    public void testPlaceWithEmptyStructure() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(3, 4, 5, 2, 1, 1));

        TestingVoxel vt = new TestingVoxel("*");
        vt.place(tile, 4, 4, 5);

        PlaceableStructure structure = new PlaceableStructure();
        structure.place(tile, 3, 4, 5);
        structure.place(tile, 4, 4, 5);

        tile.assertVoxelNull(3, 4, 5);
        tile.assertVoxel("*", 4, 4, 5);
    }

    @Test
    public void testGet() {
        Placeable vtA = new TestingVoxel("A");
        Placeable vtB = new TestingVoxel("B");

        PlaceableStructure structure = new PlaceableStructure();
        structure.set(1, 2, 3, vtA);
        structure.set(3, 2, 1, vtB);

        assertEquals(vtA, structure.get(1, 2, 3));
        assertEquals(vtB, structure.get(3, 2, 1));
        assertEquals(Nothing.INSTANCE, structure.get(10, 20, 30));
    }

    @Test
    public void testLimits() {
        Placeable vt = new TestingVoxel("X");
        PlaceableStructure structure = new PlaceableStructure();

        // Basic checks
        structure.set(1, 2, 3, vt);
        assertEquals(new WorldBBox3d(1, 2, 3, 1, 1, 1), structure.limits());

        structure.set(0, 0, 0, vt);
        assertEquals(new WorldBBox3d(0, 0, 0, 2, 3, 4), structure.limits());

        // Check adding novoxel extends limits
        structure.set(6, 7, 8, Nothing.INSTANCE);
        assertEquals(new WorldBBox3d(0, 0, 0, 7, 8, 9), structure.limits());

        // Check remove shrinks limits
        structure.remove(0, 0, 0);
        assertEquals(new WorldBBox3d(1, 2, 3, 6, 6, 6), structure.limits());

        // Check underlying placeable does not extend limits
        PlaceableStructure superStructure = new PlaceableStructure();
        superStructure.set(3, 2, 1, structure);
        assertEquals(new WorldBBox3d(3, 2, 1, 1, 1, 1), superStructure.limits());
    }
}
