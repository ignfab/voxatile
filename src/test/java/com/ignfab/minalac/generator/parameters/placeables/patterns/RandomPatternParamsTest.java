package com.ignfab.minalac.generator.parameters.placeables.patterns;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RandomPatternParamsTest {

    @Test
    public void testDeserialize() {
        // Minimal test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RandomPatternParams.class, "{ chance: 1.0, place: something }"
        ));

        // Full test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RandomPatternParams.class, "{ chance: 1.0, place: something, seed: a }"
        ));
    }

    @Test
    public void testValidate() {
        RandomPatternParams params;

        // Validating test
        params = new RandomPatternParams(TestingPlaceableParams.VALID, 0.0);
        assertDoesNotThrow(params::validate);

        // Non validating test
        params = new RandomPatternParams(TestingPlaceableParams.INVALID, 0.0);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

}
