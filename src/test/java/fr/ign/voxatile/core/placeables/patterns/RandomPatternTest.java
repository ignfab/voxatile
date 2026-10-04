package fr.ign.voxatile.core.placeables.patterns;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.placeables.TestingPlaceable;
import fr.ign.voxatile.core.utils.random.TestingRandom;
import fr.ign.voxatile.core.utils.random.TestingSeed;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.world.TestingVoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class RandomPatternTest {
    @Test
    public void testConstructor() {
        Placeable placeable = new TestingPlaceable();

        assertDoesNotThrow(() -> new RandomPattern(new TestingSeed(""), placeable, 0.5));
    }

    @Test
    public void testPlace() {
        TestingSeed seed = new TestingSeed("not empty");
        TestingRandom random = seed.random();
        TestingPlaceable placeable = new TestingPlaceable();

        RandomPattern pattern1 = new RandomPattern(seed, placeable, 0.5);
        random.setNextDouble(1.0);
        assertDoesNotThrow(() -> pattern1.place(TestingVoxelTile.UNUSED, 0, 0, 0));
        assertNull(placeable.lastPlaced());

        random.setNextDouble(0.0);
        RandomPattern pattern2 = new RandomPattern(seed, placeable, 0.5);
        assertDoesNotThrow(() -> pattern2.place(TestingVoxelTile.UNUSED, 0, 0, 0));
        assertEquals(new WorldCoords3d(0, 0, 0), placeable.lastPlaced());
    }
}
