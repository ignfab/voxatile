package com.ignfab.minalac.generator.placeables.gettable2d;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class StructureExtenderXZTest {

    @Test
    public void testGet() {
        // Capital letters represent where the structure is extendable
        /* ^
         1 | c F
         0 | B E
        -1 | a D
            ---->
            -1 0 */
        Map<WorldCoords3d, Placeable> map = new HashMap<>();
        map.put(new WorldCoords3d(-1, 0, -1), new TestingVoxel("a"));
        map.put(new WorldCoords3d(-1, 0, 0), new TestingVoxel("B"));
        map.put(new WorldCoords3d(-1, 0, 1), new TestingVoxel("c"));
        map.put(new WorldCoords3d(0, 0, -1), new TestingVoxel("D"));
        map.put(new WorldCoords3d(0, 0, 0), new TestingVoxel("E"));
        map.put(new WorldCoords3d(0, 0, 1), new TestingVoxel("F"));

        PlaceableStructure structure = new PlaceableStructure(map, 0, null, 0);

        /* ^
         3 | c F c F F
         2 | B E B E E
         1 | B E B E E
         0 | a D a D D
            ---------->
             0 1 2 3 4 */
        Gettable2d extenderA = new StructureExtenderXZ(5, 4, 2, 4, structure);
        assertLine(new char[]{'a', 'D', 'a', 'D', 'D'}, extenderA, 0);
        assertLine(new char[]{'B', 'E', 'B', 'E', 'E'}, extenderA, 1);
        assertLine(new char[]{'B', 'E', 'B', 'E', 'E'}, extenderA, 2);
        assertLine(new char[]{'c', 'F', 'c', 'F', 'F'}, extenderA, 3);
        assertEquals(Nothing.INSTANCE, extenderA.get(5, 2));
        assertEquals(Nothing.INSTANCE, extenderA.get(2, 4));

        /* ^
         1 | c c c
         0 | a a a
            ------->
             0 1 2 */
        Gettable2d extenderB = new StructureExtenderXZ(3, 2, 1, 2, structure);
        assertLine(new char[]{'a', 'a', 'a'}, extenderB, 0);
        assertLine(new char[]{'c', 'c', 'c'}, extenderB, 1);
        assertEquals(Nothing.INSTANCE, extenderB.get(3, 0));
        assertEquals(Nothing.INSTANCE, extenderB.get(1, 2));
    }

    private void assertLine(char[] pattern, Gettable2d gettable, int y) {
        for (int x = 0; x < pattern.length; x++)
            assertEquals(new TestingVoxel(String.valueOf(pattern[x])), gettable.get(x, y));
    }
}
