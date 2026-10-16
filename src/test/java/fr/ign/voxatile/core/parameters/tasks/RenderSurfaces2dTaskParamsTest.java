package fr.ign.voxatile.core.parameters.tasks;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderSurfaces2dTaskParamsTest {
    @Test
    public void testValidate() {
        RenderSurfaces2dTaskParams params;

        // Test required arguments
        params = new RenderSurfaces2dTaskParams(TestingPlaceableParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfaces2dTaskParams(TestingPlaceableParams.VALID);
        params.heightmap = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfaces2dTaskParams(TestingPlaceableParams.VALID);
        params.altitudeOffset = TestingModelValueParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfaces2dTaskParams(TestingPlaceableParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfaces2dTaskParams(TestingPlaceableParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
