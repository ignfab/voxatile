package com.ignfab.minalac.generator.placeables;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

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

        Map<WorldCoords3d, Placeable> mapStructure = new HashMap<>();

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

        // z = -1 : fill with B
        for (WorldCoords3d c : new WorldBBox3d(-1, -1, -1, 3, 3, 1))
            mapStructure.put(c, vtB);
        // z = -1 : remove the voxel at the middle -> |B B|
        mapStructure.remove(new WorldCoords3d(0, 0, -1));
        // z = -1 : remove the two voxels -> |  B|
        for (WorldCoords3d c : new WorldBBox3d(-1, -1, -1, 2, 1, 1))
            mapStructure.remove(c);

        // z = 0 | E |
        mapStructure.put(new WorldCoords3d(0, 1, 0), vtE);
        // z = 0 |CDE|
        mapStructure.put(new WorldCoords3d(-1, 0, 0), vtC);
        mapStructure.put(new WorldCoords3d(0, 0, 0), vtD);
        mapStructure.put(new WorldCoords3d(1, 0, 0), vtE);
        // z = 0 | C |
        mapStructure.put(new WorldCoords3d(0, -1, 0), vtC);

        // z = 1 |B  |
        mapStructure.put(new WorldCoords3d(-1, 1, 1), vtB);

        PlaceableStructure structure = new PlaceableStructure(mapStructure);

        // Structure is placed at the center of the tile
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

        PlaceableStructure structure = new PlaceableStructure(new HashMap<>());
        structure.place(tile, 3, 4, 5);
        structure.place(tile, 4, 4, 5);

        tile.assertVoxelNull(3, 4, 5);
        tile.assertVoxel("*", 4, 4, 5);
    }

    @Test
    public void testGet() {
        Placeable vtA = new TestingVoxel("A");
        Placeable vtB = new TestingVoxel("B");

        Map<WorldCoords3d, Placeable> mapStructure = new HashMap<>();

        mapStructure.put(new WorldCoords3d(1, 2, 3), vtA);
        mapStructure.put(new WorldCoords3d(3, 2, 1), vtB);
        PlaceableStructure structure = new PlaceableStructure(mapStructure);

        assertEquals(vtA, structure.get(1, 2, 3));
        assertEquals(vtB, structure.get(3, 2, 1));
        assertEquals(Nothing.INSTANCE, structure.get(10, 20, 30));
    }

    @Test
    public void testLimits() {
        Placeable vt = new TestingVoxel("X");
        Map<WorldCoords3d, Placeable> mapStructure = new HashMap<>();

        // Basic checks
        mapStructure.put(new WorldCoords3d(1, 2, 3), vt);
        mapStructure.put(new WorldCoords3d(0, 0, 0), vt);
        PlaceableStructure structure = new PlaceableStructure(mapStructure);

        assertEquals(new WorldBBox3d(0, 0, 0, 2, 3, 4), (new PlaceableStructure(mapStructure)).limits());

        // Check adding nothing placeable extends limits
        mapStructure.put(new WorldCoords3d(6, 7, 8), Nothing.INSTANCE);
        assertEquals(new WorldBBox3d(0, 0, 0, 7, 8, 9), (new PlaceableStructure(mapStructure)).limits());

        // Check underlying placeable does not extend limits
        Map<WorldCoords3d, Placeable> superMapStructure = new HashMap<>();
        superMapStructure.put(new WorldCoords3d(3, 2, 1), structure);
        assertEquals(new WorldBBox3d(3, 2, 1, 1, 1, 1), (new PlaceableStructure(superMapStructure)).limits());

        // Empty
        assertEquals(WorldBBox3d.EMPTY, (new PlaceableStructure(new HashMap<>())).limits());
    }
}
