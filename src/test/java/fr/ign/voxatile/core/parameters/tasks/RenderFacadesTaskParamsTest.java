package fr.ign.voxatile.core.parameters.tasks;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderFacadesTaskParamsTest {
    @Test
    public void testValidate() {
        RenderFacadesTaskParams params;

        params = new RenderFacadesTaskParams(Collections.emptyList(), TestingModelValueParams.VALID, TestingModelValueParams.VALID);
        assertDoesNotThrow(params::validate);

        // Test required arguments
        params = new RenderFacadesTaskParams(Collections.emptyList(), TestingModelValueParams.INVALID, TestingModelValueParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderFacadesTaskParams(Collections.emptyList(), TestingModelValueParams.VALID, TestingModelValueParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderFacadesTaskParams(Collections.emptyList(), TestingModelValueParams.VALID, TestingModelValueParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
