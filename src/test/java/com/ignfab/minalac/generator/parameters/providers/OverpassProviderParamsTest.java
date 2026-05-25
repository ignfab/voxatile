package com.ignfab.minalac.generator.parameters.providers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;

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
