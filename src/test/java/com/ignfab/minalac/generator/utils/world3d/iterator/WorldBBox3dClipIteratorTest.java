package com.ignfab.minalac.generator.utils.world3d.iterator;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dClipIteratorTest {
    @Test
    public void oddTest() {
        Collection<WorldCoords3d> list = List.of(
            new WorldCoords3d(1, 2, 3), // Out
            new WorldCoords3d(2, 3, 4), // In
            new WorldCoords3d(0, 0, 0), // Out
            new WorldCoords3d(6, 8, 10), // In
            new WorldCoords3d(7, 9, 11) // Out
        );

        assertBrowsesAllOnce(
            List.of(
                new WorldCoords3d(2, 3, 4),
                new WorldCoords3d(6, 8, 10)
            ),
            new WorldBBox3dClipIterator<WorldCoords3d>(list.iterator(), new WorldBBox3d(2, 3, 4, 5, 6, 7))
        );
    }

    @Test
    public void evenTest() {
        Collection<WorldCoords3d> list = List.of(
            new WorldCoords3d(2, 3, 4), // In
            new WorldCoords3d(1, 2, 3), // Out
            new WorldCoords3d(6, 8, 10), // In
            new WorldCoords3d(0, 0, 0), // Out
            new WorldCoords3d(5, 6, 7) // In
        );

        assertBrowsesAllOnce(
            List.of(
                new WorldCoords3d(2, 3, 4),
                new WorldCoords3d(6, 8, 10),
                new WorldCoords3d(5, 6, 7)
            ),
            new WorldBBox3dClipIterator<WorldCoords3d>(list.iterator(), new WorldBBox3d(2, 3, 4, 5, 6, 7))
        );
    }

    @Test
    public void emptyBBoxTest() {
        Collection<WorldCoords3d> list = List.of(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(2, 3, 4),
            new WorldCoords3d(0, 0, 0)
        );

        assertFalse(new WorldBBox3dClipIterator<WorldCoords3d>(list.iterator(), WorldBBox3d.EMPTY).hasNext());
    }
}
