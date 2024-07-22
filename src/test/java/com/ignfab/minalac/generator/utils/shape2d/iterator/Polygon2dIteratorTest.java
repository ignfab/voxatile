package com.ignfab.minalac.generator.utils.shape2d.iterator;

import com.ignfab.minalac.generator.utils.shape2d.Polyline2d;
import com.ignfab.minalac.generator.utils.iterator.IteratorTester;
import com.ignfab.minalac.generator.utils.shape2d.Polygon2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class Polygon2dIteratorTest {

    // A 2d Sandbox based on a char content
    static class Sandbox2d {
        private final char[] content;
        private final WorldBBox2d bbox;

        /**
         * Creates a new char matrix from given content and bounding box.
         *
         * @param bbox Bounding box of voxel represented in content (must match content size)
         * @param content One char per voxel, along x-axis first then y-axis (must match bbox size)
         */
        Sandbox2d(WorldBBox2d bbox, String content) {
            assertEquals(bbox.size().area(), content.length(), "Expected class constructor: given content does not match given bbox size");
            this.bbox = bbox;
            this.content = content.toCharArray();
        }

        private int index(WorldCoords2d position) {
            assertTrue(bbox.contains(position), String.format("Position out of bounds : %s", position.toString()));
            return position.x() - bbox.minX() + (position.y() - bbox.minY()) * bbox.sizeX();
        }
        /**
         * Returns a list of Voxel2d for each position that have a char present in given string.
         *
         * @param values a string containing selected chars
         *
         * @return a collection ov Voxel2d matching selected chars
         */
        public Collection<Voxel2d> voxels(String values) {
            LinkedList<Voxel2d> voxels = new LinkedList<>();

            for (WorldCoords2d position : bbox)
                if (values.indexOf(content[index(position)]) >= 0)
                    voxels.add(new Voxel2d.Impl(position));

            return voxels;
        }
    }

    @Test
    @DisplayName("Basic test with a square")
    @SuppressWarnings("checkstyle:OperatorWrap") // Allows for better alignment of ASCII-art
    public void testWithBasicPolygon() {
        Sandbox2d sandbox = new Sandbox2d(new WorldBBox2d(1, 1, 3, 3),
            // This is the simplest shape: a square
            "+++" +
            "+-+" +
            "+++");

        Polygon2d polygon = new Polygon2d(Polyline2d.fromPoints(
            new WorldCoords2d(1, 1),
            new WorldCoords2d(3, 1),
            new WorldCoords2d(3, 3),
            new WorldCoords2d(1, 3),
            new WorldCoords2d(1, 1)
        ));

        // Interface does not imply that iterator must browse voxels once but it actually does
        IteratorTester.assertBrowsesAllOnce(sandbox.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(sandbox.voxels("-+"), new Polygon2dIterator(polygon, true));
    }

    @Test
    @DisplayName("Test a polygon with holes")
    @SuppressWarnings("checkstyle:OperatorWrap") // Allows for better alignment of ASCII-art
    public void testWithPolygonWithHoles() {
        Sandbox2d sandbox = new Sandbox2d(new WorldBBox2d(0, 0, 9, 8),
            // This is a basic rectangle with two square holes
            "+++++++++" + // 0
            "+-------+" + // 1
            "+++++---+" + // 2
            "++  +---+" + // 3
            "+++++---+" + // 4
            "+----++++" + // 5
            "+----+ ++" + // 6
            "+++++++++"); // 7
        //   012345678

        Polygon2d polygon = new Polygon2d(
            // Shell
            Polyline2d.fromPoints(
                new WorldCoords2d(0, 0),
                new WorldCoords2d(8, 0),
                new WorldCoords2d(8, 7),
                new WorldCoords2d(0, 7),
                new WorldCoords2d(0, 0)
            ),
            // Holes
            Polyline2d.fromPoints(
                new WorldCoords2d(1, 2),
                new WorldCoords2d(4, 2),
                new WorldCoords2d(4, 4),
                new WorldCoords2d(1, 4),
                new WorldCoords2d(1, 2)
            ),
            Polyline2d.fromPoints(
                new WorldCoords2d(5, 5),
                new WorldCoords2d(5, 7),
                new WorldCoords2d(7, 7),
                new WorldCoords2d(7, 5),
                new WorldCoords2d(5, 5)
            )
        );

        // Interface does not imply that iterator must browse voxels once but it actually does
        IteratorTester.assertBrowsesAllOnce(sandbox.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(sandbox.voxels("-+"), new Polygon2dIterator(polygon, true));
    }

    @Test
    @DisplayName("Test empty polygon")
    public void testEmpty() {
        Polygon2d polygon = new Polygon2d(Polyline2d.fromPoints(new WorldCoords2d(1, 2), new WorldCoords2d(1, 2), new WorldCoords2d(1, 2)));
        IteratorTester.assertBrowsesAllOnce(Collections.singleton(new Voxel2d.Impl(new WorldCoords2d(1, 2))), new Polygon2dIterator(polygon, true));
        IteratorTester.assertBrowsesAllOnce(Collections.emptyList(), new Polygon2dIterator(polygon, false));
    }

    @Test
    @DisplayName("Test various oblique lines")
    @SuppressWarnings("checkstyle:OperatorWrap") // Allows for better alignment of ASCII-art
    public void testObliqueLines() {
        /* Here, we will test various oblique lines, trying to cover every possible cases.
         * Same angle has to be tested in one direction and it's oposite. We use square so
         * we test at once two oposite directions as well as both perpendicular ones.
         *
         * Legend of ASCII diagrams:
         * o is for vertices (only there to ease reading, no influence on tests)
         * + is for eges
         * - is for inside
        */

        Sandbox2d expected;
        Polygon2d polygon;

        // Straight lines
        polygon = new Polygon2d(Polyline2d.fromPoints(
            new WorldCoords2d(0, 0),
            new WorldCoords2d(0, 6),
            new WorldCoords2d(6, 6),
            new WorldCoords2d(6, 0),
            new WorldCoords2d(0, 0)
        ));

        expected = new Sandbox2d(new WorldBBox2d(0, 0, 7, 7),
            "o+++++o" + // 0
            "+-----+" + // 1
            "+-----+" + // 2
            "+-----+" + // 3
            "+-----+" + // 4
            "+-----+" + // 5
            "o+++++o"); // 6
        //   0123456

        IteratorTester.assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // 45° lines
        polygon = new Polygon2d(Polyline2d.fromPoints(
            new WorldCoords2d(3, 0),
            new WorldCoords2d(6, 3),
            new WorldCoords2d(3, 6),
            new WorldCoords2d(0, 3),
            new WorldCoords2d(3, 0)
        ));

        expected = new Sandbox2d(new WorldBBox2d(0, 0, 7, 7),
            "   o   " + // 0
            "  +-+  " + // 1
            " +---+ " + // 2
            "o-----o" + // 3
            " +---+ " + // 4
            "  +-+  " + // 5
            "   o   "); // 6
       //    0123456

        IteratorTester.assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // Between straight and 45° (first cases)
        polygon = new Polygon2d(Polyline2d.fromPoints(
            new WorldCoords2d(5, 0),
            new WorldCoords2d(6, 5),
            new WorldCoords2d(1, 6),
            new WorldCoords2d(0, 1),
            new WorldCoords2d(5, 0)
        ));

        expected = new Sandbox2d(new WorldBBox2d(0, 0, 7, 7),
            "   ++o " + // 0
            "o++--+ " + // 1
            "+----+ " + // 2
            "+-----+" + // 3
            " +----+" + // 4
            " +--++o" + // 5
            " o++   "); // 6
        //   0123456

        IteratorTester.assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // Between straight and 45° (other cases)
        polygon = new Polygon2d(Polyline2d.fromPoints(
            new WorldCoords2d(1, 0),
            new WorldCoords2d(6, 1),
            new WorldCoords2d(5, 6),
            new WorldCoords2d(0, 5),
            new WorldCoords2d(1, 0)
        ));

        expected = new Sandbox2d(new WorldBBox2d(0, 0, 7, 7),
            " o++   " + // 0
            " +--++o" + // 1
            " +----+" + // 2
            "+-----+" + // 3
            "+----+ " + // 4
            "o++--+ " + // 5
            "   ++o "); // 6
        //   0123456

        IteratorTester.assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        IteratorTester.assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));
    }
}
