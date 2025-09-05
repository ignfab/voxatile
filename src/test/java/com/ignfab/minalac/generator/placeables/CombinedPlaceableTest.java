package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class CombinedPlaceableTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(CombinedPlaceable::new);
    }

    @Test
    public void testAdd() {
        CombinedPlaceable combined = new CombinedPlaceable();

        assertDoesNotThrow(() -> combined.add(new TestingPlaceable()));
        assertDoesNotThrow(() -> combined.add(new TestingPlaceable()));
    }

    @Test
    public void testPlace() {
        TestingPlaceable placeable1 = new TestingPlaceable();
        TestingPlaceable placeable2 = new TestingPlaceable();

        CombinedPlaceable combined0 = new CombinedPlaceable();
        CombinedPlaceable combined1 = new CombinedPlaceable();
        CombinedPlaceable combined2 = new CombinedPlaceable();

        combined1.add(placeable1);

        combined2.add(placeable1);
        combined2.add(placeable2);

        // Test without any child
        assertDoesNotThrow(() -> combined0.place(TestingGenerationTile.UNUSED, 1, 2, 3));

        // Test with one child
        assertDoesNotThrow(() -> combined1.place(TestingGenerationTile.UNUSED, 4, 5, 6));
        assertEquals(new WorldCoords3d(4, 5, 6), placeable1.lastPlaced());

        // Test with two children
        assertDoesNotThrow(() -> combined2.place(null, 7, 8, 9));
        assertEquals(new WorldCoords3d(7, 8, 9), placeable1.lastPlaced());
        assertEquals(new WorldCoords3d(7, 8, 9), placeable2.lastPlaced());
    }
}
