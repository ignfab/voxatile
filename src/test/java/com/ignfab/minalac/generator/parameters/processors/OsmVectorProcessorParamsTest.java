package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class OsmVectorProcessorParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        OsmProcessorParams params = new OsmProcessorParams();
        assertDoesNotThrow(() -> params.create(generation));
    }
}
