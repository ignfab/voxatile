package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.TestingVoxel;
import com.ignfab.minalac.generator.world.TestingVoxelTile;

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
    public void testBbox() {
        Placeable p = new TestingVoxel("X");
        CombinedPlaceable placeable = new CombinedPlaceable();
        PlaceableStructure.Builder s1 = new PlaceableStructure.Builder();
        PlaceableStructure.Builder s2 = new PlaceableStructure.Builder();
        PlaceableStructure.Builder s3 = new PlaceableStructure.Builder();
        s1.set(1, 0, 0, p);
        s2.set(0, -2, 0, p);
        s3.set(0, 0, 3, p);

        // Empty placable
        assertEquals(WorldBBox3d.EMPTY, placeable.bbox());

        // One structure
        placeable.add(s1.build());
        assertEquals(new WorldBBox3d(1, 0, 0, 1, 1, 1), placeable.bbox());

        // All structures
        placeable.add(s2.build());
        placeable.add(s3.build());
        assertEquals(new WorldBBox3d(0, -2, 0, 2, 3, 4), placeable.bbox());
    }
}
