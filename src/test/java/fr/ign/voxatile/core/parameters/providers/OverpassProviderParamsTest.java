package fr.ign.voxatile.core.parameters.providers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class OverpassProviderParamsTest {
    @Test
    public void testCreate() throws NoSuchAuthorityCodeException, FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        OverpassProviderParams params = new OverpassProviderParams("https://example.org", "feature1");
        assertDoesNotThrow(() -> params.create(generation));
    }
}
