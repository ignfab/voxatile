package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.world.TestingVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class GeoToolsVectorProcessorParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");

        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, crs2154, 0, 0, 20, 20, 1, 1, 0.0, 100);

        // A simple OK test
        GeoToolsVectorProcessorParams params = new GeoToolsVectorProcessorParams();
        assertDoesNotThrow(() -> params.create(generation));
    }
}
