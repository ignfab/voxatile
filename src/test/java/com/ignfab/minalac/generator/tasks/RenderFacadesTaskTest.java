package com.ignfab.minalac.generator.tasks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapStore;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.gettable2d.Gettable2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class RenderFacadesTaskTest {
    private WorldBBox3d bbox;
    private TestingVoxelTile tile;
    private Heightmap heightmap;
    private ModelStore models;

    @BeforeEach
    void setUp() {
        bbox = new WorldBBox3d(-2, -3, 0, 5, 6, 40);
        tile = new TestingVoxelTile(bbox);
        models = new ModelStore();
        heightmap = new Heightmap(tile.limits().to2d(), 0);

        // Creates a sloped heightmap
        /*  y
            ^
          2 | 1  2  3  4  5
          1 | 0  1  2  3  4
          0 |-1  0  1  2  3
         -1 |-2 -1  0  1  2
         -2 |-3 -2 -1  0  1
         -3 |-4 -3 -2 -1  0
            -----------------> x
             -2 -1  0  1  2 */
        for (WorldCoords2d c : heightmap.bbox()) {
            int value = (c.x() - heightmap.bbox().minX()) + (c.y() - heightmap.bbox().minY()) - 4;
            heightmap.set(c.x(), c.y(), value);
        }
    }

    @Test
    public void testRender() {
        throw new RuntimeException("Update it");
        /*  y
            ^
          2 |
          1 |    *  *  *
          0 |    *     *
         -1 |    *     *
         -2 |    *  *  *
         -3 |
            -----------------> x
             -2 -1  0  1  2 */
//        WorldBBox2d modelBbox = new WorldBBox2d(-1, -2, 3, 4);
//        Model building = new TestingRectangleShapeVoxelizable2dModel(modelBbox);
//
//        int expectedHeight = 20;
//        building.setMetadata("height", expectedHeight);
//        models.add("building", building);
//
//        assertDoesNotThrow(() -> new RenderFacadesTask(
//            new ModelSelection("building", new ModelFilterHasMetadata("height")),
//            heightmap,
//            "height",
//            new PlaceableStructure(createSimpleStructure(2, 2, new TestingVoxel("ground")), 0, null, 0),
//            new PlaceableStructure(createSimpleStructure(2, 2, new TestingVoxel("upperFloor")), 0, null, null)
//        ).run(tile));
//
//        for (WorldCoords3d c : tile.limits()) {
//            int x = c.x();
//            int y = c.y();
//            int z = c.z();
//
//            int minZFacade = heightmap.get(c.x(), c.y()) + 1;
//            int maxZFacade = minZFacade + expectedHeight - 1;
//
//            if (minZFacade <= z && z <= maxZFacade) {
//                /*  y
//                    ^
//                  2 |
//                  1 |    A  B  A
//                  0 |    A     A
//                 -1 |    A     A
//                 -2 |    A  B  A
//                 -3 |
//                    -----------------> x
//                     -2 -1  0  1  2 */
//
//                if (-2 <= y && y <= 1) {
//                    // voxels in 'A'
//                    if (x == -1 || x == 1)
//                        tile.assertVoxelNotNull(x, y, z);
//                    // voxels in 'B'
//                    else if (x == 0 && (y == -2 || y == 1))
//                        tile.assertVoxelNotNull(x, y, z);
//                    else
//                        tile.assertVoxelNull(x, y, z);
//                } else {
//                    tile.assertVoxelNull(x, y, z);
//                }
//            } else {
//                tile.assertVoxelNull(x, y, z);
//            }
//        }
    }

    // The tests bellow should be moved when "layout" creator class is made
    @Test
    public void testCreateSimpleLayoutFromStructure() {
        /* ^
         2 | G H I
         1 | D E F
         0 | A B C
            ------>
             0 1 2 */
        Map<WorldCoords3d, Placeable> mapStructureGround = createStructure(3, 3, 'A');

        /* ^
         0 | I J K
            ------>
             0 1 2 */
        Map<WorldCoords3d, Placeable> mapStructureFloor = createStructure(3, 1, 'I');

        assertDoesNotThrow(
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                10,
                10,
                new PlaceableStructure(mapStructureGround, 0, null, 0),
                new PlaceableStructure(mapStructureFloor, 0, null, null)
            ));

        // ground x-axis not extendable
        InvocationTargetException e = assertThrows(
            InvocationTargetException.class,
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                10,
                10,
                new PlaceableStructure(mapStructureGround, null, null, 0),
                new PlaceableStructure(mapStructureFloor, 0, null, null)
            ));
        assertInstanceOf(UnsupportedOperationException.class, e.getCause());

        // ground z-axis not extendable
        e = assertThrows(
            InvocationTargetException.class,
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                10,
                10,
                new PlaceableStructure(mapStructureGround, 0, null, null),
                new PlaceableStructure(mapStructureFloor, 0, null, null)
            ));
        assertInstanceOf(UnsupportedOperationException.class, e.getCause());

        // floor x-axis not extendable
        e = assertThrows(
            InvocationTargetException.class,
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                10,
                10,
                new PlaceableStructure(mapStructureGround, 0, null, 0),
                new PlaceableStructure(mapStructureFloor, null, null, null)
            ));
        assertInstanceOf(UnsupportedOperationException.class, e.getCause());

        // There is not enough height for both
        /* ^
         1 | G H G H
         0 | A B A B
            -------->
             0 1 2 3 */
        Gettable2d groundOnly = assertDoesNotThrow(
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                4,
                // height ground is 2, floor is 1, not enough height for both
                2,
                new PlaceableStructure(mapStructureGround, 2, null, 1),
                new PlaceableStructure(mapStructureFloor, 0, null, null)
            ));
        assertLine(new char[]{'A', 'B', 'A', 'B'}, groundOnly, 0);
        assertLine(new char[]{'G', 'H', 'G', 'H'}, groundOnly, 1);

        /* ^
         2 | I K I K
         1 | G H G H
         0 | A B A B
            -------->
             0 1 2 3 */
        Gettable2d both = assertDoesNotThrow(
            () -> invokeCreateSimpleLayoutFromStructure(
                new RenderFacadesTask(null, null, null, null, null),
                4,
                // height ground is 2, floor is 1, not enough height for both
                3,
                new PlaceableStructure(mapStructureGround, 2, null, 1),
                new PlaceableStructure(mapStructureFloor, 1, null, null)
            ));
        assertLine(new char[]{'A', 'B', 'A', 'B'}, both, 0);
        assertLine(new char[]{'G', 'H', 'G', 'H'}, both, 1);
        assertLine(new char[]{'I', 'K', 'I', 'K'}, both, 2);
    }

    private void assertLine(char[] pattern, Gettable2d gettable, int y) {
        for (int x = 0; x < pattern.length; x++)
            assertEquals(new TestingVoxel(String.valueOf(pattern[x])), gettable.get(x, y));
    }

    private static Gettable2d invokeCreateSimpleLayoutFromStructure(RenderFacadesTask renderFacadesTask, int lineLength, int height, PlaceableStructure groundFloorPattern, PlaceableStructure upperFloorPattern) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = RenderFacadesTask.class.getDeclaredMethod("createSimpleLayoutFromStructure", int.class, int.class, PlaceableStructure.class, PlaceableStructure.class);
        method.setAccessible(true);
        return (Gettable2d) method.invoke(renderFacadesTask, lineLength, height, groundFloorPattern, upperFloorPattern);
    }

    private static Map<WorldCoords3d, Placeable> createStructure(int lengthX, int lengthZ, char start) {
        Map<WorldCoords3d, Placeable> mapStructure = new HashMap<>();
        for (int z = 0; z < lengthZ; z++)
            for (int x = 0; x < lengthX; x++)
                mapStructure.put(new WorldCoords3d(x, 0, z), new TestingVoxel(String.valueOf((char) (start + x + z * lengthX))));
        return mapStructure;
    }

    private static Map<WorldCoords3d, Placeable> createSimpleStructure(int lengthX, int lengthZ, Placeable placeable) {
        Map<WorldCoords3d, Placeable> mapStructure = new HashMap<>();
        for (int z = 0; z < lengthZ; z++)
            for (int x = 0; x < lengthX; x++)
                mapStructure.put(new WorldCoords3d(x, 0, z), placeable);
        return mapStructure;
    }
}
