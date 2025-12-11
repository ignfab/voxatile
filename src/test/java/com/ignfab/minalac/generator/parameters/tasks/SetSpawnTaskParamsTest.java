package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class SetSpawnTaskParamsTest {
    @Test
    public void testValidate() {
        assertDoesNotThrow(new SetSpawnTaskParams(TestingHeightmapParams.VALID, 5, -1)::validate);
        assertThrows(IllegalArgumentException.class, new SetSpawnTaskParams(TestingHeightmapParams.INVALID, 2, 1)::validate);
    }

    @Test
    public void testDeserialize() {
        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("setSpawn", SetSpawnTaskParams.class);

        SetSpawnTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            SetSpawnTaskParams.class,
            """
            type: setSpawn
            heightmap: ground
            x: 2
            y: 5
            """,
            builder
        ));

        assertInstanceOf(WritableHeightmapParams.class, params.heightmap);
        assertEquals(2, params.x);
        assertEquals(5, params.y);
    }
    @Test
    public void testCreate() {
        // Provided BBOX must be centered at (0, 0) (See TestingGenerationTile(WorldBBox3d limits))
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(-1, -2, 0, 3, 4, 5));
        tile.newStoredHeightmap("ground", 0);

        SetSpawnTaskParams withinLimits = new SetSpawnTaskParams(new TestingHeightmapParams("ground"), 0, 1);
        assertDoesNotThrow(() -> withinLimits.create(tile.generation()));

        SetSpawnTaskParams outOfBoundSpawn = new SetSpawnTaskParams(new TestingHeightmapParams("ground"), -3, -4);
        assertThrows(IllegalArgumentException.class, () -> outOfBoundSpawn.create(tile.generation()));
    }
}
