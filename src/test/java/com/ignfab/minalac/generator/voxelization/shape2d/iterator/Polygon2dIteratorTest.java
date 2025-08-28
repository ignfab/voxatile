package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class Polygon2dIteratorTest {

    // A 2d Sandbox based on a char content
    private static class Sandbox2d {
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
            assertTrue(bbox.contains(position), String.format("Position out of bounds: %s", position));
            return position.x() - bbox.minX() + (position.y() - bbox.minY()) * bbox.sizeX();
        }

        /**
         * Returns a list of Positioned2d for each position that have a char
         * present in given string.
         *
         * @param values a string containing selected chars
         *
         * @return a collection of Positioned2d matching selected chars
         */
        public Collection<Positioned2d> voxels(String values) {
            LinkedList<Positioned2d> voxels = new LinkedList<>();

            for (WorldCoords2d position : bbox)
                if (values.indexOf(content[index(position)]) >= 0)
                    voxels.add(position);

            return voxels;
        }
    }

    @Test
    @DisplayName("Test intersection")
    public void testIntersection() {
        Segment2d segment;
        Polygon2dIterator.Intersection intersection;

        // Horizontal segment
        segment = new Segment2d(
            new WorldCoords2d(30, 20),
            new WorldCoords2d(-10, 20)
        );

        assertNull(Polygon2dIterator.intersection(segment, 19));
        assertNull(Polygon2dIterator.intersection(segment, 21));
        intersection = Polygon2dIterator.intersection(segment, 20);

        assertEquals(-10, intersection.start());
        assertEquals(30, intersection.end());
        assertFalse(intersection.bottom());
        assertFalse(intersection.top());

        // Vertical segment
        segment = new Segment2d(
            new WorldCoords2d(15, -5),
            new WorldCoords2d(15, 10)
        );

        assertNull(Polygon2dIterator.intersection(segment, -6));
        assertNull(Polygon2dIterator.intersection(segment, 11));

        intersection = Polygon2dIterator.intersection(segment, 0);
        assertNotNull(intersection);
        assertEquals(15, intersection.start());
        assertEquals(15, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());

        intersection = Polygon2dIterator.intersection(segment, -5);
        assertNotNull(intersection);
        assertTrue(intersection.bottom());
        assertFalse(intersection.top());

        intersection = Polygon2dIterator.intersection(segment, 10);
        assertNotNull(intersection);
        assertFalse(intersection.bottom());
        assertTrue(intersection.top());

        // One voxel segment
        segment = new Segment2d(
            new WorldCoords2d(10, -20),
            new WorldCoords2d(10, -20)
        );
        assertNull(Polygon2dIterator.intersection(segment, -21));
        assertNull(Polygon2dIterator.intersection(segment, -19));
        assertNotNull(Polygon2dIterator.intersection(segment, -20));

        // Several voxel intersections
        segment = new Segment2d(
            new WorldCoords2d(-5, -2),
            new WorldCoords2d(5, 2)
        );
        intersection = Polygon2dIterator.intersection(segment, 0);
        assertEquals(-1, intersection.start());
        assertEquals(1, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());

        // Same test, the other way
        segment = new Segment2d(
            new WorldCoords2d(-5, 2),
            new WorldCoords2d(5, -2)
        );
        intersection = Polygon2dIterator.intersection(segment, 0);
        assertEquals(-1, intersection.start());
        assertEquals(1, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());
    }

    @Test
    @DisplayName("Basic test with a square")
    @SuppressWarnings("checkstyle:OperatorWrap") // Allows for better alignment of ASCII-art
    public void testWithBasicPolygon() {
        Sandbox2d sandbox = new Sandbox2d(new WorldBBox2d(0, 0, 5, 5),
            // This is the simplest shape: a square
            "     " +
            " +++ " +
            " +-+ " +
            " +++ " +
            "     ");

        Polygon2d polygon = new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(1, 1),
            new WorldCoords2d(3, 1),
            new WorldCoords2d(3, 3),
            new WorldCoords2d(1, 3)
        ));

        // Interface does not imply that iterator must browse voxels once but it actually does
        assertBrowsesAllOnce(sandbox.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(sandbox.voxels("-+"), new Polygon2dIterator(polygon, true));
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
            LinearRing2d.fromPoints(
                new WorldCoords2d(0, 0),
                new WorldCoords2d(8, 0),
                new WorldCoords2d(8, 7),
                new WorldCoords2d(0, 7)
            ),
            // Holes
            LinearRing2d.fromPoints(
                new WorldCoords2d(1, 2),
                new WorldCoords2d(4, 2),
                new WorldCoords2d(4, 4),
                new WorldCoords2d(1, 4)
            ),
            LinearRing2d.fromPoints(
                new WorldCoords2d(5, 5),
                new WorldCoords2d(5, 7),
                new WorldCoords2d(7, 7),
                new WorldCoords2d(7, 5)
            )
        );

        // Interface does not imply that iterator must browse voxels once but it actually does
        assertBrowsesAllOnce(sandbox.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(sandbox.voxels("-+"), new Polygon2dIterator(polygon, true));
    }

    @Test
    @DisplayName("Test empty polygon")
    public void testEmpty() {
        Polygon2d polygon = new Polygon2d(LinearRing2d.fromPoints(new WorldCoords2d(1, 2), new WorldCoords2d(1, 2), new WorldCoords2d(1, 2)));
        assertBrowsesAllOnce(Collections.emptyList(), new Polygon2dIterator(polygon, true));
        assertBrowsesAllOnce(Collections.emptyList(), new Polygon2dIterator(polygon, false));
    }

    @Test
    @DisplayName("Test various oblique lines")
    @SuppressWarnings("checkstyle:OperatorWrap") // Allows for better alignment of ASCII-art
    public void testObliqueLines() {
        /* Here, we will test various oblique lines, trying to cover every possible cases.
         * Same angle has to be tested in one direction and its opposite. We use square so
         * we test at once two opposite directions as well as both perpendicular ones.
         *
         * Legend of ASCII diagrams:
         * o is for vertices (only there to ease reading, no influence on tests)
         * + is for edges
         * - is for inside
         */

        Sandbox2d expected;
        Polygon2d polygon;

        // Straight lines
        polygon = new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(0, 0),
            new WorldCoords2d(0, 6),
            new WorldCoords2d(6, 6),
            new WorldCoords2d(6, 0)
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

        assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // 45° lines
        polygon = new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(3, 0),
            new WorldCoords2d(6, 3),
            new WorldCoords2d(3, 6),
            new WorldCoords2d(0, 3)
        ));

        expected = new Sandbox2d(new WorldBBox2d(0, 0, 7, 7),
            "   o   " + // 0
            "  +-+  " + // 1
            " +---+ " + // 2
            "o-----o" + // 3
            " +---+ " + // 4
            "  +-+  " + // 5
            "   o   "); // 6
        //   0123456

        assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // Between straight and 45° (first cases)
        polygon = new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(5, 0),
            new WorldCoords2d(6, 5),
            new WorldCoords2d(1, 6),
            new WorldCoords2d(0, 1)
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

        assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));

        // Between straight and 45° (other cases)
        polygon = new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(1, 0),
            new WorldCoords2d(6, 1),
            new WorldCoords2d(5, 6),
            new WorldCoords2d(0, 5)
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

        assertBrowsesAllOnce(expected.voxels("-"), new Polygon2dIterator(polygon, false));
        assertBrowsesAllOnce(expected.voxels("-+o"), new Polygon2dIterator(polygon, true));
    }
}
