package com.ignfab.minalac.generator.placeables;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.world.VoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class CompoundPlaceableTest {
    private record DummyCompoundPlaceable(Collection<Placeable> components) implements CompoundPlaceable {
        @Override
        public void place(VoxelTile tile, int x, int y, int z) {} // Unused here
    }

    @Test
    public void testPalette() {
        TestingVoxel voxel1 = new TestingVoxel("1");
        TestingVoxel voxel2 = new TestingVoxel("2");
        TestingVoxel voxel3 = new TestingVoxel("3");

        // Test without any child
        CompoundPlaceable compound0 = new DummyCompoundPlaceable(List.of());
        assertTrue(compound0.palette().isEmpty(), "Palette should be empty");

        // Test with one child
        CompoundPlaceable compound1 = new DummyCompoundPlaceable(List.of(voxel1));
        Set<Placeable> palette1 = compound1.palette();
        assertEquals(1, palette1.size(), "Palette should contain one element");
        assertEquals(voxel1, palette1.iterator().next(), "Palette should contain the voxel");

        // Test with two children
        CompoundPlaceable compound2 = new DummyCompoundPlaceable(List.of(voxel1, voxel2));
        Set<Placeable> palette2 = compound2.palette();
        assertEquals(2, palette2.size(), "Palette should contain two elements");
        assertTrue(palette2.contains(voxel1), "Palette should contain the first voxel");
        assertTrue(palette2.contains(voxel2), "Palette should contain the second voxel");

        // Test with repeated children
        CompoundPlaceable compound3 = new DummyCompoundPlaceable(List.of(voxel3, voxel3));
        Set<Placeable> palette3 = compound3.palette();
        assertEquals(1, palette3.size(), "Palette should contain one element");
        assertEquals(voxel3, palette3.iterator().next(), "Palette should contain the voxel");

        // Test with nested children (including repeated)
        CompoundPlaceable compound4 = new DummyCompoundPlaceable(List.of(voxel1, compound2, voxel3));
        Set<Placeable> palette4 = compound4.palette();
        assertEquals(3, palette4.size(), "Palette should contain three elements");
        assertTrue(palette4.contains(voxel1), "Palette should contain the first voxel");
        assertTrue(palette4.contains(voxel2), "Palette should contain the second voxel");
        assertTrue(palette4.contains(voxel3), "Palette should contain the third voxel");
    }
}
