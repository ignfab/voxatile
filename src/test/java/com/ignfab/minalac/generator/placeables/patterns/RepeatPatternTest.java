package com.ignfab.minalac.generator.placeables.patterns;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.testing.TestingVoxel;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatPatternTest {
    private static final TestingVoxel X = new TestingVoxel("X");

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RepeatPattern(
            new PlaceableStructure(),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0)
        ));
    }

    @Test
    public void testRepeat() {
        PlaceableStructure struc;
        RepeatPattern pattern;

        TestingVoxel o = new TestingVoxel("O");
        TestingVoxel y = new TestingVoxel("Y");
        struc = new PlaceableStructure();
        struc.set(0, 0, 0, o);
        struc.set(1, 0, 0, X);
        struc.set(2, 0, 0, y);

        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0)
        );
        assertEquals(o, pattern.get(-3, 0, 0));
        assertEquals(X, pattern.get(-2, 0, 0));
        assertEquals(y, pattern.get(-1, 0, 0));
        assertEquals(o, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(1, 0, 0));
        assertEquals(y, pattern.get(2, 0, 0));
        assertEquals(o, pattern.get(6, 0, 0));
        assertEquals(X, pattern.get(7, 0, 0));
        assertEquals(y, pattern.get(8, 0, 0));

        struc = new PlaceableStructure();
        struc.set(0, 0, 0, o);
        struc.set(0, 1, 0, X);
        struc.set(0, 2, 0, y);

        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0)
        );
        assertEquals(o, pattern.get(0, -3, 0));
        assertEquals(X, pattern.get(0, -2, 0));
        assertEquals(y, pattern.get(0, -1, 0));
        assertEquals(o, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(0, 1, 0));
        assertEquals(y, pattern.get(0, 2, 0));
        assertEquals(o, pattern.get(0, 6, 0));
        assertEquals(X, pattern.get(0, 7, 0));
        assertEquals(y, pattern.get(0, 8, 0));

        struc = new PlaceableStructure();
        struc.set(0, 0, 0, o);
        struc.set(0, 0, 1, X);
        struc.set(0, 0, 2, y);

        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0)
        );
        assertEquals(o, pattern.get(0, 0, -3));
        assertEquals(X, pattern.get(0, 0, -2));
        assertEquals(y, pattern.get(0, 0, -1));
        assertEquals(o, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(0, 0, 1));
        assertEquals(y, pattern.get(0, 0, 2));
        assertEquals(o, pattern.get(0, 0, 6));
        assertEquals(X, pattern.get(0, 0, 7));
        assertEquals(y, pattern.get(0, 0, 8));
    }

    @Test
    public void testShift() {
        RepeatPattern pattern;
        PlaceableStructure struc;

        // Each Z -> Shift X and Y
        struc = new PlaceableStructure();
        struc.set(0, 0, 0, X);
        struc.set(2, 2, 0, Nothing.INSTANCE);
        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(1, 2, 0)
        );
        assertEquals(X, pattern.get(-1, -2, -1));
        assertEquals(X, pattern.get(-4, -2, -1));
        assertEquals(X, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(3, 0, 0));
        assertEquals(X, pattern.get(1, 2, 1));
        assertEquals(X, pattern.get(4, 2, 1));
        assertEquals(X, pattern.get(2, 1, 2));
        assertEquals(X, pattern.get(5, 1, 2));

        // Each Y -> Shift X and Z
        struc = new PlaceableStructure();
        struc.set(0, 0, 0, X);
        struc.set(2, 0, 2, Nothing.INSTANCE);
        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(1, 0, 2),
            new WorldCoords3d(0, 0, 0)
        );
        assertEquals(X, pattern.get(-1, -1, -2));
        assertEquals(X, pattern.get(-4, -1, -2));
        assertEquals(X, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(3, 0, 0));
        assertEquals(X, pattern.get(1, 1, 2));
        assertEquals(X, pattern.get(4, 1, 2));
        assertEquals(X, pattern.get(2, 2, 1));
        assertEquals(X, pattern.get(5, 2, 1));


        // Each X -> Shift Y and Z
        struc = new PlaceableStructure();
        struc.set(0, 0, 0, X);
        struc.set(0, 2, 2, Nothing.INSTANCE);
        pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(0, 2, 1),
            new WorldCoords3d(0, 0, 0),
            new WorldCoords3d(0, 0, 0)
        );
        assertEquals(X, pattern.get(-1, -2, -1));
        assertEquals(X, pattern.get(-1, -2, -4));
        assertEquals(X, pattern.get(0, 0, 0));
        assertEquals(X, pattern.get(0, 0, 3));
        assertEquals(X, pattern.get(1, 2, 1));
        assertEquals(X, pattern.get(1, 2, 4));
        assertEquals(X, pattern.get(2, 1, 2));
        assertEquals(X, pattern.get(2, 1, 5));
    }

    public void testSpacing() {
        PlaceableStructure struc = new PlaceableStructure();
        struc.set(0, 0, 0, X);

        RepeatPattern pattern = new RepeatPattern(
            struc,
            new WorldCoords3d(1, 0, 0),
            new WorldCoords3d(0, 2, 0),
            new WorldCoords3d(0, 0, 3)
        );
        assertEquals(X, pattern.get(0, 0, 0));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 1, 0));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 2, 0));
        assertEquals(X, pattern.get(0, 3, 0));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 4, 0));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 5, 0));
        assertEquals(X, pattern.get(0, 6, 0));

        assertEquals(Nothing.INSTANCE, pattern.get(1, 0, 0));
        assertEquals(X, pattern.get(2, 0, 0));
        assertEquals(Nothing.INSTANCE, pattern.get(3, 0, 0));
        assertEquals(X, pattern.get(4, 0, 0));

        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 1));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 2));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 3));
        assertEquals(X, pattern.get(0, 0, 4));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 5));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 6));
        assertEquals(Nothing.INSTANCE, pattern.get(0, 0, 7));
        assertEquals(X, pattern.get(0, 0, 8));
    }
}
