package fr.ign.voxatile.core.parameters.heightmaps;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantHeightmapParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new TestingGeneration();

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
