package com.ignfab.minalac.generator.parameters.heightmaps;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.world.TestingVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class CappedManhattanHeightmapParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("ground", 0));

        CappedManhattanHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            CappedManhattanHeightmapParams.class,
            """
            manhattan: ground
            maximumDistance: 5
            targetValue: 4
            """
        ));
        assertInstanceOf(WritableHeightmapParams.class, params.manhattan);
        assertEquals(5, params.maximumDistance);
        assertEquals(4, params.targetValue);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation.heightmaps()));

        assertThrows(JacksonException.class,
            () -> ParamsTester.deserialize(
                CappedManhattanHeightmapParams.class,
                """
                manhattan:
                maximumDistance: 5
                targetValue: 4
                """
        ));
    }

    @Test
    public void testValidate() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new CappedManhattanHeightmapParams(
                    TestingHeightmapParams.INVALID
                ).validate()
        );

        CappedManhattanHeightmapParams paramsWithDistanceInvalid = new CappedManhattanHeightmapParams(
            new CappedManhattanHeightmapParams(TestingHeightmapParams.VALID)
        );

        paramsWithDistanceInvalid.maximumDistance = -1;
        assertThrows(IllegalArgumentException.class, paramsWithDistanceInvalid::validate);
    }

}
