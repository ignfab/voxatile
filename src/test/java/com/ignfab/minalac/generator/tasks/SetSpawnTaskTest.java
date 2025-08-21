package com.ignfab.minalac.generator.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetSpawnTaskTest {

    @Test
    public void testSetSpawn() {
        // Provided BBOX must be centered at (0, 0) (See TestingGenerationTile(WorldBBox3d limits))
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(-1, -2, 0, 3, 4, 5));
        TestingHeightmap ground = tile.newStoredHeightmap("ground", 0);
        ground.set(-1, 1, 3);

        WorldCoords3d defaultSpawn = tile.generation().world().getMetadata().getSpawn();

        new SetSpawnTask(ground.spec(), new WorldCoords2d(10, 10)).run(tile);
        assertEquals(defaultSpawn, tile.generation().world().getMetadata().getSpawn());

        new SetSpawnTask(ground.spec(), new WorldCoords2d(-1, 1)).run(tile);
        assertEquals(new WorldCoords3d(-1, 1, 3), tile.generation().world().getMetadata().getSpawn());
    }
}
