package com.ignfab.minalac.generator.tasks;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.TestingRectangleShape2dModel;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class CopyHeightmapTaskTest {
    @Test
    public void testRenderHeightmapSameBoundingBox() {
        WorldBBox3d bbox = new WorldBBox3d(-1, -1, 0, 3, 4, 1);
        TestingGenerationTile tile = new TestingGenerationTile(bbox);
        TestingHeightmap from = tile.newStoredHeightmap("from", 0);
        TestingHeightmap to = tile.newStoredHeightmap("to", 0);

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

        int value = 1;
        for (int y = 2; y >= -1; y--)
            for (int x = -1; x <= 1; x++) {
                from.set(x, y, value);
                value++;
            }

        Model model = new TestingRectangleShape2dModel(new WorldBBox2d(0, 1, 2, 2));
        tile.models().add("square", List.of(model));
        ModelSelection selection = new ModelSelection("square", null);

        new CopyHeightmapTask(selection, from.spec(), to.spec()).run(tile);

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
        WorldBBox3d bbox = new WorldBBox3d(-1, -1, 0, 3, 4, 1);
        TestingGenerationTile tile = new TestingGenerationTile(bbox);

        TestingHeightmap from = tile.newStoredHeightmap("from", new WorldBBox2d(-1, -1, 3, 4), 1);

        TestingHeightmap to = tile.newStoredHeightmap("to", new WorldBBox2d(-1, -3, 3, 4), 2);

        Model model = new TestingRectangleShape2dModel(new WorldBBox2d(0, 0, 2, 3));
        tile.models().add("rectangle", List.of(model));
        ModelSelection selection = new ModelSelection("rectangle", null);

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

        new CopyHeightmapTask(selection, from.spec(), to.spec()).run(tile);

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
