package fr.ign.voxatile.core.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLines2dTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLines2dTaskParams params;

        // Check any invalid members makes valid throw
        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.INVALID, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // All valid
        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
