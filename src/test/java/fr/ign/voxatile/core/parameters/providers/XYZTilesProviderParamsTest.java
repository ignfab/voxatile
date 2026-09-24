package fr.ign.voxatile.core.parameters.providers;

import org.junit.jupiter.api.Test;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class XYZTilesProviderParamsTest {

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new XYZTilesProviderParams("https://example.com", 1));
    }

    @Test
    void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:4326"));

        assertDoesNotThrow(() -> new XYZTilesProviderParams("https://example.com/{z}/{x}/{y}.png", 1).create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZTilesProviderParams("", 1).create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZTilesProviderParams(null, 1).create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZTilesProviderParams("https://example.com", 0).create(generation));
        assertThrows(IllegalArgumentException.class, () -> new XYZTilesProviderParams("https://example.com/{z}/{x}/{y}.png", -1).create(generation));
    }
}
