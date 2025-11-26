package fr.ign.voxatile.core.parameters.tasks;

import java.io.File;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.utils.random.TestingSeed;
import fr.ign.voxatile.core.world.TestingVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class SaveMinimapTaskParamsTest {
    @Test
    void testDeserialize() {
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            SaveMinimapTaskParams.class,
            """
            type: saveMinimap
            minimap: test
            format: jpeg
            destination: testFile.png
            background: [1, 2, 3, 4]
            """,
            ParamsTester.mapperBuilderWithParams("saveMinimap", SaveMinimapTaskParams.class)
        ));
    }

    @Test
    void testValidate() {
        SaveMinimapTaskParams params = new SaveMinimapTaskParams(" ", new File("test.png"), "png");
        assertThrows(IllegalArgumentException.class, params::validate);
        params = new SaveMinimapTaskParams("minimap", new File(" "), "png");
        assertThrows(IllegalArgumentException.class, params::validate);
        params = new SaveMinimapTaskParams("minimap", new File("test.unknown"), "unknown");
        assertThrows(IllegalArgumentException.class, params::validate);
        params = new SaveMinimapTaskParams("minimap", new File("testFile.png"), "jpeg");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new SaveMinimapTaskParams("minimap", new File("testFile.pNG"), "png");
        assertDoesNotThrow(params::validate);
        params = new SaveMinimapTaskParams("minimap", new File("testFile.png"), "png");
        assertDoesNotThrow(params::validate);
    }

    @Test
    void testCreate() {
        SaveMinimapTaskParams params = new SaveMinimapTaskParams("minimap",  new File("testFile.png"), "png");
        assertDoesNotThrow(() -> params.create(
            new Generation(new TestingVoxelWorld(new File("testing")), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100)
        ));
    }
}
