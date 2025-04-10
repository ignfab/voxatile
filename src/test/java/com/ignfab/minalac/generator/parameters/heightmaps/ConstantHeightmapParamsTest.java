package com.ignfab.minalac.generator.parameters.heightmaps;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ConstantHeightmapParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);

        ReadableHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            ReadableHeightmapParams.class,
            "4")
        );
        ConstantHeightmapParams chp = assertInstanceOf(ConstantHeightmapParams.class, params);
        assertEquals(4, chp.constant);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation.heightmaps()));

        ReadableHeightmapParams cst = assertDoesNotThrow(() -> ParamsTester.deserialize(
            ReadableHeightmapParams.class,
            "constant: 7")
        );
        chp = assertInstanceOf(ConstantHeightmapParams.class, cst);
        assertEquals(7, chp.constant);
    }
}
