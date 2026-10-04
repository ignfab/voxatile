package fr.ign.voxatile.core.parameters.providers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class WMSFloatBilProviderParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        WMSFloatBilProviderParams params = new WMSFloatBilProviderParams("https://example.org", "layer1");
        assertDoesNotThrow(() -> params.create(generation));

        // Wrong CRS test
        params.crs = "ThisIsNotACrs";
        assertThrows(RuntimeException.class, () -> params.create(generation));
    }
}
