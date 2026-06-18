package fr.ign.voxatile.core.parameters.utils;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.MismatchedInputException;

import fr.ign.voxatile.core.parameters.ParamsTester;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class WorldCoords3dParamsTest {
    @Test
    void testDeserialization() {
        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldCoords3dParams.class, "[]"));

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldCoords3dParams.class, "[1, 2]"));

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(WorldCoords3dParams.class, "[1, 2, 3, 4]"));

        WorldCoords3dParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(WorldCoords3dParams.class, "[1, 2, 3]"));
        WorldCoords3d coords = assertDoesNotThrow(params::create);
        assertEquals(new WorldCoords3d(1, 2, 3), coords);
    }
}
