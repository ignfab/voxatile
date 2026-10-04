package fr.ign.voxatile.core.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.cfg.MapperBuilder;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.generation.heightmaps.HeightmapDeclaration;
import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.heightmaps.WritableHeightmapParams;
import fr.ign.voxatile.core.parameters.models.ModelSelectionParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;

import static org.junit.jupiter.api.Assertions.*;

public class CopyHeightmapTaskParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new TestingGeneration();
        generation.heightmaps().add(new HeightmapDeclaration("ground", 5));
        generation.heightmaps().add(new HeightmapDeclaration("water", 1));

        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("copyHeightmap", CopyHeightmapTaskParams.class);

        CopyHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            CopyHeightmapTaskParams.class,
            """
            type: copyHeightmap
            models:
              type: water
            from: water
            to: ground
            """,
            builder
        ));
        assertInstanceOf(ModelSelectionParams.class, params.models);
        assertInstanceOf(WritableHeightmapParams.class, params.from);
        assertEquals("ground", params.to.stored);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));

        assertDoesNotThrow(
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                from: water
                to: ground
                """,
                builder
            )
        );

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                models:
                  type: water
                to: ground
                """,
                builder
            )
        );

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                models:
                  type: water
                from: water
                """,
                builder
            )
        );
    }

    @Test
    public void testValidate() {

        CopyHeightmapTaskParams paramsWithInvalidFrom = new CopyHeightmapTaskParams(
            TestingHeightmapParams.INVALID,
            TestingHeightmapParams.VALID
        );

        paramsWithInvalidFrom.models = TestingModelSelectionParams.VALID;

        assertThrows(IllegalArgumentException.class, paramsWithInvalidFrom::validate);

        CopyHeightmapTaskParams paramsWithInvalidTo = new CopyHeightmapTaskParams(
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.INVALID
        );

        paramsWithInvalidTo.models = TestingModelSelectionParams.VALID;

        assertThrows(IllegalArgumentException.class, paramsWithInvalidTo::validate);
    }
}
