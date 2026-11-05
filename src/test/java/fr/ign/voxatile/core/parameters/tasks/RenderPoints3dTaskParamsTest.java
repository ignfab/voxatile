package fr.ign.voxatile.core.parameters.tasks;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderPoints3dTaskParamsTest {

    @Test
    public void testValidate() {
        RenderPoints3dTaskParams params;

        // Check any invalid member makes validation throw
        params = new RenderPoints3dTaskParams(TestingPlaceableStructureParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderPoints3dTaskParams(TestingPlaceableStructureParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // All valid
        params = new RenderPoints3dTaskParams(TestingPlaceableStructureParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
