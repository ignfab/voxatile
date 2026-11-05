package fr.ign.voxatile.core.parameters.providers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.parameters.processors.Image2FloatMatrixProcessorParams;

import static org.junit.jupiter.api.Assertions.*;

public class XYZFloatTilesProviderParamsTest {

    @Test
    void testConstructor() {
        XYZFloatTilesProviderParams params = new XYZFloatTilesProviderParams("https://example.com");
        assertEquals("https://example.com", params.url);
        assertEquals(0, params.zoomMin);
        assertEquals(12, params.zoomMax);
    }

    @Test
    void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:4326"));

        assertDoesNotThrow(() -> new XYZFloatTilesProviderParams("https://example.com/{z}/{x}/{y}.png").create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZFloatTilesProviderParams("").create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZFloatTilesProviderParams(null).create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZFloatTilesProviderParams("https://example.com").create(generation));
    }

    @Test
    void testZoomRange() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:4326"));
        XYZFloatTilesProviderParams params = new XYZFloatTilesProviderParams("https://example.com/{z}/{x}/{y}.png");

        params.zoomMin = -1;
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
        params.zoomMin = 0;
        params.zoomMax = -1;
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
        params.zoomMin = 2;
        params.zoomMax = 1;
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
        params.zoomMax = 2;
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    void testDefaultProcessor() {
        XYZFloatTilesProviderParams params = new XYZFloatTilesProviderParams("https://example.com/{z}/{x}/{y}.png");

        assertInstanceOf(Image2FloatMatrixProcessorParams.class, params.defaultProcessor());
    }
}
