package fr.ign.voxatile.core.parameters.placeables.patterns;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.parameters.placeables.TestingPlaceableParams;
import fr.ign.voxatile.core.placeables.TestingPlaceable;

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
        params = new RandomPatternParams(new TestingPlaceableParams(new TestingPlaceable()), 0.0);
        assertDoesNotThrow(params::validate);

        // Non validating test
        params = new RandomPatternParams(new TestingPlaceableParams(null), 0.0);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

}
