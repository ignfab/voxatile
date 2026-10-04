package fr.ign.voxatile.core.parameters.tasks;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderPoints2dTaskParamsTest {

    @Test
    public void testValidate() {
        RenderPoints2dTaskParams params;

        // Check any invalid member makes validation throw
        params = new RenderPoints2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderPoints2dTaskParams(TestingPlaceableStructureParams.INVALID, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderPoints2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // All valid
        params = new RenderPoints2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
