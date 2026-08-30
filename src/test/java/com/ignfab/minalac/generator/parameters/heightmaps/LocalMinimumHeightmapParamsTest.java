package com.ignfab.minalac.generator.parameters.heightmaps;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class LocalMinimumHeightmapParamsTest {

    @Test
    public void testDeserialize() {
        Generation generation = new TestingGeneration();
        generation.heightmaps().add(new HeightmapDeclaration("ground", 0));

        LocalMinimumHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            LocalMinimumHeightmapParams.class,
            """
            localMin: ground
            range: 3
            """
        ));
        assertInstanceOf(WritableHeightmapParams.class, params.localMin);
        assertEquals(3, params.range);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation.heightmaps()));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                LocalMinimumHeightmapParams.class,
                """
                localMin:
                range: 3
                """
                )
        );

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                LocalMinimumHeightmapParams.class,
                """
                localMin: ground
                """
            )
        );
    }

    @Test
    public void testValidate() {
        LocalMinimumHeightmapParams paramsWithInvalidBase = new LocalMinimumHeightmapParams(
            TestingHeightmapParams.INVALID,
            3
        );
        assertThrows(IllegalArgumentException.class, paramsWithInvalidBase::validate);

        LocalMinimumHeightmapParams paramsWithInvalidRange = new LocalMinimumHeightmapParams(
            TestingHeightmapParams.VALID,
            -1
            );
        assertThrows(IllegalArgumentException.class, paramsWithInvalidRange::validate);
    }
}
