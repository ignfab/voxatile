package com.ignfab.minalac.generator.generation;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class SquareUnitsTileGeneratorTest {

    private void testConfiguration(String name, int blocksize, WorldBBox2d bbox, int maxTileSize) {

        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(blocksize, bbox);
        List<WorldBBox2d> tiles = tileGenerator.getTiles(maxTileSize);

        // All tiles shoud be inside bbox
        for (WorldBBox2d tile : tiles)
            assertTrue(bbox.contains(tile), "%s: %s should be contained in bbox(%s)".formatted(name, tile, bbox));

        // No tile should overlap
        for (WorldBBox2d tile1 : tiles)
            for (WorldBBox2d tile2 : tiles)
                if (tile1 != tile2)
                    assertFalse(tile1.intersects(tile2), "%s: %s and %s should not intersect".formatted(name, tile1, tile2));

        // No tile should be larger than expected
        for (WorldBBox2d tile : tiles) {
            assertTrue(tile.size().x() <= maxTileSize, "%s: %s should not be wider that expected (%d)".formatted(name, tile, maxTileSize));
            assertTrue(tile.size().y() <= maxTileSize, "%s: %s should not be taller that expected (%d)".formatted(name, tile, maxTileSize));
        }

        // No tile boundary inside a mapblock (except edges)
        for (WorldBBox2d tile : tiles) {
            if (tile.minX() != bbox.minX())
                assertTrue(tile.minX() % blocksize == 0, "%s: %s minX should be aligned to block".formatted(name, tile));
            if (tile.minY() != bbox.minY())
                assertTrue(tile.minY() % blocksize == 0, "%s: %s minY should be aligned to block".formatted(name, tile));
            if (tile.maxX() != bbox.maxX())
                assertTrue((tile.maxX() + 1) % blocksize == 0, "%s: %s maxX should be aligned to block".formatted(name, tile));
            if (tile.maxY() != bbox.maxY())
                assertTrue((tile.maxY() + 1) % blocksize == 0, "%s: %s maxY should be aligned to block".formatted(name, tile));
        }

        // The whole box should be covered
        int y = bbox.minY();
        while (y < bbox.maxY()) {

            // Select tiles intersecting axis at Y
            List<WorldBBox2d> tilesSelection = new LinkedList<>();
            int minY = bbox.maxY();
            for (WorldBBox2d tile : tiles)
                if (tile.minY() <= y && tile.maxY() >= y) {
                    minY = Math.min(minY, tile.maxY());
                    tilesSelection.add(tile);
                }

            // Sort tiles on X axis now
            tilesSelection.sort(Comparator.comparingInt(WorldBBox2d::minX));

            // Check that the whole given Y axis is covered
            // ie: tiles starts at minX, touches each other and ends at maxX
            int x = bbox.minX();
            for (WorldBBox2d tile : tilesSelection) {
                assertTrue(x >= tile.minX(), "%s: All given area should be covered by tiles (found hole starting at x=%d, y=%d)".formatted(name, x, y));
                x = tile.maxX() + 1;
            }
            assertTrue(x > bbox.maxX(), "%s: All given area should be covered by tiles (found hole starting at x=%d, y=%d)".formatted(name, x, y));

            // We can now jump to next Y
            // ie: smallest maxY of all tiles previously selected
            y = minY + 1;
        }
    }

    @Test
    public void variousCasesTest() {
        // Random cases
        testConfiguration("random1", 16, new WorldBBox2d(-23, -76, 123, 153), 50);
        testConfiguration("random2", 32, new WorldBBox2d(-56, -23, 435, 345), 75);

        // Aligned cases
        testConfiguration("aligned0", 10, new WorldBBox2d(10, 10, 199, 199), 50);
        testConfiguration("aligned1", 10, new WorldBBox2d(10, 10, 200, 199), 50);
        testConfiguration("aligned2", 10, new WorldBBox2d(10, 10, 199, 200), 50);
        testConfiguration("aligned3", 10, new WorldBBox2d(10, 19, 199, 199), 50);
        testConfiguration("aligned4", 10, new WorldBBox2d(19, 10, 199, 199), 50);

        // Block sized tiles
        testConfiguration("block", 10, new WorldBBox2d(10, 10, 99, 99), 10);
    }

    @Test
    public void singleTileCaseTest() {
        // If bbox is smaller than wanted size, only one tile should be returned
        WorldBBox2d bbox = new WorldBBox2d(-2, -7, 12, 15);
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(16, bbox);
        List<WorldBBox2d> tiles = tileGenerator.getTiles(30);
        assertEquals(1, tiles.size());
        assertEquals(bbox, tiles.getFirst());
    }

    @Test
    public void distributionTest() {
        // Hard to check surface is "fairly" distributed but some obvious cases could be tested
        WorldBBox2d bbox = new WorldBBox2d(0, 0, 180, 100);
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(10, bbox);
        List<WorldBBox2d> tiles = tileGenerator.getTiles(70);
        assertEquals(6, tiles.size());
        assertEquals(tiles.get(0).size().area(), tiles.get(1).size().area());
        assertEquals(tiles.get(1).size().area(), tiles.get(2).size().area());
        assertEquals(tiles.get(2).size().area(), tiles.get(3).size().area());
        assertEquals(tiles.get(3).size().area(), tiles.get(4).size().area());
        assertEquals(tiles.get(4).size().area(), tiles.get(5).size().area());
    }

    @Test
    public void getTilesTest() {
        WorldBBox2d bbox = new WorldBBox2d(10, 20, 30, 40);
        SquareUnitsTileGenerator tileGenerator = new SquareUnitsTileGenerator(16, bbox);
        assertThrows(IllegalArgumentException.class, () -> tileGenerator.getTiles(8));
    }
}
