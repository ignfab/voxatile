package com.ignfab.minalac.generator.placeables;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelTile;
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
    public void testPalette() {
        TestingVoxel voxel1 = new TestingVoxel("1");
        TestingVoxel voxel2 = new TestingVoxel("2");
        TestingVoxel voxel3 = new TestingVoxel("3");

        CombinedPlaceable combined0 = new CombinedPlaceable();
        CombinedPlaceable combined1 = new CombinedPlaceable();
        CombinedPlaceable combined2 = new CombinedPlaceable();
        CombinedPlaceable combined3 = new CombinedPlaceable();
        CombinedPlaceable combined4 = new CombinedPlaceable();

        combined1.add(voxel1);

        combined2.add(voxel1);
        combined2.add(voxel2);

        combined3.add(voxel3);
        combined3.add(voxel3);

        combined4.add(voxel1);
        combined4.add(combined2);
        combined4.add(voxel3);

        // Test without any child
        assertTrue(combined0.palette().isEmpty(), "Palette should be empty");

        // Test with one child
        Set<Placeable> palette1 = combined1.palette();
        assertEquals(1, palette1.size(), "Palette should contain one element");
        assertEquals(voxel1, palette1.iterator().next(), "Palette should contain the voxel");

        // Test with two children
        Set<Placeable> palette2 = combined2.palette();
        assertEquals(2, palette2.size(), "Palette should contain two elements");
        assertTrue(palette2.contains(voxel1), "Palette should contain the first voxel");
        assertTrue(palette2.contains(voxel2), "Palette should contain the second voxel");

        // Test with repeated children
        Set<Placeable> palette3 = combined3.palette();
        assertEquals(1, palette3.size(), "Palette should contain one element");
        assertEquals(voxel3, palette3.iterator().next(), "Palette should contain the voxel");

        // Test with nested children (including repeated)
        Set<Placeable> palette4 = combined4.palette();
        assertEquals(3, palette4.size(), "Palette should contain three elements");
        assertTrue(palette4.contains(voxel1), "Palette should contain the first voxel");
        assertTrue(palette4.contains(voxel2), "Palette should contain the second voxel");
        assertTrue(palette4.contains(voxel3), "Palette should contain the third voxel");
    }
}
