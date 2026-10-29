package fr.ign.voxatile.core.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLines2dTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLines2dTaskParams params;

        // Check any invalid members makes valid throw
        params = new RenderLines2dTaskParams(TestingHeightmapParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingHeightmapParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // Missing test invalid layout

        // All valid
        params = new RenderLines2dTaskParams(TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);
    }
}
