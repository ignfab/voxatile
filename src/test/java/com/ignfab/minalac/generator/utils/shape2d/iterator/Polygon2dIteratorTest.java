package com.ignfab.minalac.generator.utils.shape2d.iterator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.shape2d.PolyLine2d;
import com.ignfab.minalac.generator.utils.shape2d.Polygon2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Iterator;

public class Polygon2dIteratorTest {

    // A 2d Sandbox based on a char content
    class SandBox2d {
        private char[] content;
        private int width;
        private int height;

        SandBox2d(int width, int height, String content) {
            assertEquals(width * height, content.length(), "Expected class constructor: given content does not match given width/height");
            this.width = width;
            this.height = height;
            this.content = content.toCharArray();
        }

        private int index(int x, int y) {
            assertTrue(x >= 0 && x < width && y >= 0 && y < height, String.format("Coordinates out of bounds : (%d, %d)", x, y));
            return x + y * width;
        }

        public void testIterator(Iterator<Voxel2d> iterator, String name, char oldVal, char newVal) {
            while (iterator.hasNext()) {
                Voxel2d voxel = iterator.next();
                int i = index(voxel.coords().x(), voxel.coords().y());
                if (content[i] != oldVal)
                    fail(String.format("Got unwanted position (%d , %d) from iterator \"%s\"", voxel.coords().x(), voxel.coords().y(), name));
                content[i] = newVal;
            }

            // Count there is no remaining position
            int count = 0;
            for (int i = 0; i < height * width; i++)
                if (content[i] == oldVal)
                    count++;
            assertEquals(0, count, String.format("Iterator \"%s\" skipped %d positions", name, count));
        }
    }

    @Test
    @DisplayName("Basic test with a square")
    public void testWithBasicPolygon() {
        SandBox2d sandBox = new SandBox2d(5, 5,
            "     "
          + " +++ "
          + " +-+ "
          + " +++ "
          + "     "
        );

        PolyLine2d shell = PolyLine2d.fromPoints(
            new WorldCoords2d(1, 1),
            new WorldCoords2d(3, 1),
            new WorldCoords2d(3, 3),
            new WorldCoords2d(1, 3),
            new WorldCoords2d(1, 1)
        );

        Polygon2d polygon = new Polygon2d(shell, Collections.emptyList());

        sandBox.testIterator(new Polygon2dIterator(polygon, false), "inside", '-', '+');
        sandBox.testIterator(new Polygon2dIterator(polygon, true), "all", '+', 'X');
    }

    @Test
    @DisplayName("Test a polygon with holes")
    public void testWithPolygonWithHoles() {
        SandBox2d sandBox = new SandBox2d(9, 8,
          // 012345678
            "+++++++++" // 0
          + "+-------+" // 1
          + "+++++---+" // 2
          + "++  +---+" // 3
          + "+++++---+" // 4
          + "+----++++" // 5
          + "+----+ ++" // 6
          + "+++++++++" // 7
        );

        Polygon2d polygon = new Polygon2d(
            PolyLine2d.fromPoints(
                new WorldCoords2d(0, 0),
                new WorldCoords2d(8, 0),
                new WorldCoords2d(8, 7),
                new WorldCoords2d(0, 7),
                new WorldCoords2d(0, 0)),
            PolyLine2d.fromPoints(
                new WorldCoords2d(1, 2),
                new WorldCoords2d(4, 2),
                new WorldCoords2d(4, 4),
                new WorldCoords2d(1, 4),
                new WorldCoords2d(1, 2)),
            PolyLine2d.fromPoints(
                new WorldCoords2d(5, 5),
                new WorldCoords2d(5, 7),
                new WorldCoords2d(7, 7),
                new WorldCoords2d(7, 5),
                new WorldCoords2d(5, 5))
        );

        sandBox.testIterator(new Polygon2dIterator(polygon, false), "inside", '-', '+');
        sandBox.testIterator(new Polygon2dIterator(polygon, true), "all", '+', 'X');
    }

    @Test
    @DisplayName("Test various oblique lines")
    public void testObliqueLines() {

        SandBox2d sandBox = new SandBox2d(14, 14,
          // This is a powerfull symbol that was used by Maïa people
          // to create talismans againts ancient computer bugs
          // 01234567890123
            "+++++   +    +" // 0
          + " +--+   ++  ++" // 1
          + "  +--+ +-+ +-+" // 2
          + "   +-+ +--+--+" // 3
          + " ++---+-----++" // 4
          + "++----+---++  " // 5
          + "  ++----++    " // 6
          + "    ++----++  " // 7
          + "  ++---+----++" // 8
          + "++-----+---++ " // 9
          + "+--+--+ +-+   " // 10
          + "+-+ +-+ +--+  " // 11
          + "++  ++   +--+ " // 12
          + "+    +   +++++" // 13
        );

            // Actualy, this geometry has four different lines types,
            // flat, less than 45°, 45° and more than 45°, in four orientations.
            Polygon2d polygon = new Polygon2d(PolyLine2d.fromPoints(
                new WorldCoords2d(0, 0),
                new WorldCoords2d(4, 0),
                new WorldCoords2d(6, 5),
                new WorldCoords2d(8, 0),
                new WorldCoords2d(10, 3),
                new WorldCoords2d(13, 0),
                new WorldCoords2d(13, 4),
                new WorldCoords2d(8, 6),
                new WorldCoords2d(13, 8),
                new WorldCoords2d(10, 10),
                new WorldCoords2d(13, 13),
                new WorldCoords2d(9, 13),
                new WorldCoords2d(7, 8),
                new WorldCoords2d(5, 13),
                new WorldCoords2d(3, 10),
                new WorldCoords2d(0, 13),
                new WorldCoords2d(0, 9),
                new WorldCoords2d(5, 7),
                new WorldCoords2d(0, 5),
                new WorldCoords2d(3, 3),
                new WorldCoords2d(0, 0))
            );

            // Test without border, should correspond to all '-'
            sandBox.testIterator(new Polygon2dIterator(polygon, false), "inside", '-', '+');

            // Now all '-' have been replaced by '+', we can test including borders
            sandBox.testIterator(new Polygon2dIterator(polygon, true), "all", '+', 'X');
    }
}
