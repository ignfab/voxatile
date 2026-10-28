package fr.ign.voxatile.core.parameters.tasks;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.parameters.heightmaps.TestingHeightmapParams;
import fr.ign.voxatile.core.parameters.models.TestingModelSelectionParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderSurfacesTaskParamsTest {
    @Test
    public void testValidate() {
        RenderSurfacesTaskParams params;

        // Test required arguments
        params = new RenderSurfacesTaskParams(TestingPlaceableParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingPlaceableParams.VALID);
        params.heightmap = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingPlaceableParams.VALID);
        params.altitudeOffset = TestingModelValueParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingPlaceableParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingPlaceableParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
