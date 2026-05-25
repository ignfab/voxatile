package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class GeoToolsVectorProcessorParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        final GeoToolsVectorProcessorParams params = new GeoToolsVectorProcessorParams();
        assertDoesNotThrow(() -> params.create(generation));
    }
}
