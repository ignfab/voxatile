package fr.ign.voxatile.core.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLines3dTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLines3dTaskParams params;

        // Test required arguments
        params = new RenderLines3dTaskParams();
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines3dTaskParams();
        params.place = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        // Missing test invalid layout

        params = new RenderLines3dTaskParams();
        params.place = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);
    }
}
