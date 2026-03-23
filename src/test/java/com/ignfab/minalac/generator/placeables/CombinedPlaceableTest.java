package com.ignfab.minalac.generator.placeables;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class CombinedPlaceableTest {
    @Test
    public void testEmptyBuilder() {
        assertSame(CombinedPlaceable.EMPTY, CombinedPlaceable.builder().build());
    }

    @Test
    public void testAdd() {
        CombinedPlaceable.Builder combined = CombinedPlaceable.builder();

        assertDoesNotThrow(() -> combined.add(new TestingPlaceable()));
        assertDoesNotThrow(() -> combined.add(new TestingPlaceable()));
    }

    @Test
    public void testPlace() {
        TestingPlaceable placeable1 = new TestingPlaceable();
        TestingPlaceable placeable2 = new TestingPlaceable();

        CombinedPlaceable combined0 = CombinedPlaceable.EMPTY;
        CombinedPlaceable combined1 = CombinedPlaceable.builder().add(placeable1).build();
        CombinedPlaceable combined2 = CombinedPlaceable.builder()
            .add(placeable1)
            .add(placeable2)
            .build();

        // Test without any child
        assertDoesNotThrow(() -> combined0.place(TestingVoxelTile.UNUSED, 1, 2, 3));

        // Test with one child
        assertDoesNotThrow(() -> combined1.place(TestingVoxelTile.UNUSED, 4, 5, 6));
        assertEquals(new WorldCoords3d(4, 5, 6), placeable1.lastPlaced());

        // Test with two children
        assertDoesNotThrow(() -> combined2.place(null, 7, 8, 9));
        assertEquals(new WorldCoords3d(7, 8, 9), placeable1.lastPlaced());
        assertEquals(new WorldCoords3d(7, 8, 9), placeable2.lastPlaced());
    }

    @Test
    public void testComponents() {
        TestingPlaceable placeable1 = new TestingPlaceable();
        TestingPlaceable placeable2 = new TestingPlaceable();

        CombinedPlaceable combined0 = CombinedPlaceable.EMPTY;
        CombinedPlaceable combined1 = CombinedPlaceable.builder().add(placeable1).build();
        CombinedPlaceable combined2 = CombinedPlaceable.builder()
            .add(placeable1)
            .add(placeable2)
            .build();

        // Test without any child
        assertTrue(combined0.components().isEmpty(), "Components should be empty");

        // Test with one child
        Collection<Placeable> components1 = combined1.components();
        assertEquals(1, components1.size(), "Components should contain one element");
        assertEquals(placeable1, components1.iterator().next(), "Components should contain the placeable");

        // Test with two children
        Collection<Placeable> components2 = combined2.components();
        assertEquals(2, components2.size(), "Components should contain two elements");
        assertTrue(components2.contains(placeable1), "Components should contain the first placeable");
        assertTrue(components2.contains(placeable2), "Components should contain the second placeable");
    }
}
