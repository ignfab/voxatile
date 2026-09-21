package com.ignfab.minalac.generator.parameters.fetchers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class WFSFetcherParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        WFSFetcherParams params = new WFSFetcherParams("https://example.org", "feature1");
        assertDoesNotThrow(() -> params.create(generation));

        // Wrong CRS test
        params.crs = "ThisIsNotACrs";
        assertThrows(RuntimeException.class, () -> params.create(generation));
    }
}
