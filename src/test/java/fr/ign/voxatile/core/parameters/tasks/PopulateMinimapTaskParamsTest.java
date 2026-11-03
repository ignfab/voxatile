package fr.ign.voxatile.core.parameters.tasks;

import java.awt.Color;
import java.util.Map;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.utils.random.TestingSeed;
import fr.ign.voxatile.core.world.TestingVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class PopulateMinimapTaskParamsTest {
    @Test
    void testDeserialize() {
        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("populateMinimap", PopulateMinimapTaskParams.class);

        PopulateMinimapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            PopulateMinimapTaskParams.class,
            """
            type: populateMinimap
            minimap: test
            colors:
              a: "#FFFFFF"
              b: [128, 182, 10, 128]
              c: [255, 0, 0]
            """,
            builder
        ));
        assertEquals("test", params.minimap);
    }

    @Test
    void testValidate() {
        PopulateMinimapTaskParams params = new PopulateMinimapTaskParams(" ", Map.of("voxel", Color.BLACK));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new PopulateMinimapTaskParams("minimap", Map.of("voxel", Color.BLACK));
        assertDoesNotThrow(params::validate);
    }

    @Test
    void testCreate() {
        PopulateMinimapTaskParams params = new PopulateMinimapTaskParams("minimap", Map.of("voxel", Color.BLACK));
        assertDoesNotThrow(() -> params.create(
            new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100)
        ));
    }
}
