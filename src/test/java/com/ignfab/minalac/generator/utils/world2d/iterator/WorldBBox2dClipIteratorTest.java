package com.ignfab.minalac.generator.utils.world2d.iterator;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox2dClipIteratorTest {
    @Test
    public void oddTest() {
        Collection<WorldCoords2d> list = List.of(
            new WorldCoords2d(1, 2), // Out
            new WorldCoords2d(2, 3), // In
            new WorldCoords2d(0, 0), // Out
            new WorldCoords2d(6, 8), // In
            new WorldCoords2d(7, 9) // Out
        );

        assertBrowsesAllOnce(
            List.of(
                new WorldCoords2d(2, 3),
                new WorldCoords2d(6, 8)
            ),
            new WorldBBox2dClipIterator<WorldCoords2d>(list.iterator(), new WorldBBox2d(2, 3, 5, 6))
        );
    }

    @Test
    public void evenTest() {
        Collection<WorldCoords2d> list = List.of(
            new WorldCoords2d(2, 3), // In
            new WorldCoords2d(1, 2), // Out
            new WorldCoords2d(6, 8), // In
            new WorldCoords2d(0, 0), // Out
            new WorldCoords2d(5, 6) // In
        );

        assertBrowsesAllOnce(
            List.of(
                new WorldCoords2d(2, 3),
                new WorldCoords2d(6, 8),
                new WorldCoords2d(5, 6)
            ),
            new WorldBBox2dClipIterator<WorldCoords2d>(list.iterator(), new WorldBBox2d(2, 3, 5, 6))
        );
    }

    @Test
    public void emptyBBoxTest() {
        Collection<WorldCoords2d> list = List.of(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(2, 3),
            new WorldCoords2d(0, 0)
        );

        assertFalse(new WorldBBox2dClipIterator<WorldCoords2d>(list.iterator(), WorldBBox2d.EMPTY).hasNext());
    }
}
