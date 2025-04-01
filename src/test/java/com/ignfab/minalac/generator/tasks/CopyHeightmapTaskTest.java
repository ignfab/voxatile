package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.models.TestingRectangleShapeVoxelizable2dModel;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CopyHeightmapTaskTest {
    @Test
    public void testRenderHeightmapSameBoundingBox() {
        WorldBBox2d bboxMap = new WorldBBox2d(-1, -1, 3, 4);
        /*
          ^
          y
         2|  1  2  3
         1|  4  5  6
         0|  7  8  9
        -1| 10 11 12
          |---------x>
            -1  0  1
        */
        Heightmap from = new Heightmap(bboxMap, 0);
        int value = 1;
        for (int y = 2; y >= -1; y--)
            for (int x = -1; x <= 1; x++) {
                from.set(x, y, value);
                value++;
            }

        Heightmap to = new Heightmap(bboxMap, 0);

        Model model = new TestingRectangleShapeVoxelizable2dModel(new WorldBBox2d(0, 1, 2, 2));
        ModelStore store = new ModelStore();
        store.add("square", model);
        ModelSelection selection = new ModelSelection(store, "square", null);

        TileTask copyRdr = new CopyHeightmapTask(selection, from, to);
        copyRdr.run(bboxMap.to3d(-1, 1));

        // Excepted outcome
        /*
          ^
          y
         2|  0  2  3
         1|  0  5  6
         0|  0  0  0
        -1|  0  0  0
          |---------x>
            -1  0  1
        */

        assertValue(0, to, -1, 2);
        assertValue(2, to, 0, 2);
        assertValue(3, to, 1, 2);
        assertValue(0, to, -1, 1);
        assertValue(5, to, 0, 1);
        assertValue(6, to, 1, 1);
        assertValue(0, to, -1, 0);
        assertValue(0, to, 0, 0);
        assertValue(0, to, 1, 0);
        assertValue(0, to, -1, -1);
        assertValue(0, to, 0, -1);
        assertValue(0, to, 1, -1);
    }

    @Test
    public void testRenderHeightmapDifferentBoundingBox() {
        Heightmap from = new Heightmap(new WorldBBox2d(-1, -1, 3, 4), 1);
        Heightmap to = new Heightmap(new WorldBBox2d(-1, -3, 3, 4), 2);

        Model model = new TestingRectangleShapeVoxelizable2dModel(new WorldBBox2d(0, 0, 2, 3));
        ModelStore store = new ModelStore();
        store.add("rectangle", model);
        ModelSelection selection = new ModelSelection(store, "rectangle", null);

        // Below
        //   - 1 represents BBOX of heightmap from
        //   - 2 represents BBOX of heightmap to
        //   - 6 represents BBOX of the model
        //   - 7 represents intersection of from and model (1 + 6)
        //   - 3 represents intersection of to and from (2 + 1)
        //   - 9 represents intersection of to, from and model (2 + 1 + 6)
        /*
          ^
          y
         3|  1  1  1
         2|  1  1  1
         1|  1  7  7  6
         0|  3  9  9  6
         1|  3  3  3
        -2|  2  2  2
        -3|  2  2  2
          |---------------x>
            -1  0  1  2
        */

        TileTask copyRdr = new CopyHeightmapTask(selection, from, to);
        // Rendering area covers all BBOX
        copyRdr.run(new WorldBBox3d(-1, -3, 0, 4, 6, 1));

        // Excepted outcome (Replaced values should be where the "9" are located)
        /*
          ^
          y
         0|  2  1  1
         1|  2  2  2
        -2|  2  2  2
        -3|  2  2  2
          |---------x>
            -1  0  1
        */
        assertValue(2, to, -1, -3);
        assertValue(2, to, 0, -3);
        assertValue(2, to, 1, -3);
        assertValue(2, to, -1, -2);
        assertValue(2, to, 0, -2);
        assertValue(2, to, 1, -2);
        assertValue(2, to, -1, -1);
        assertValue(2, to, 0, -1);
        assertValue(2, to, 1, -1);
        assertValue(2, to, -1, 0);
        assertValue(1, to, 0, 0);
        assertValue(1, to, 1, 0);
    }

    private void assertValue(int expected, ReadableHeightmap heightmap, int x, int y) {
        assertEquals(expected, heightmap.get(x, y), String.format("at (x = %d, y = %d)", x, y));
    }
}
