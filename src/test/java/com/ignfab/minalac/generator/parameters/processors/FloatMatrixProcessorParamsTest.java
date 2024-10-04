package com.ignfab.minalac.generator.parameters.processors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class FloatMatrixProcessorParamsTest {
    @Test
    public void testCreate() throws NoSuchAuthorityCodeException, FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
        CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");

        Generation generation = new Generation(
            new TestingVoxelWorld(
                new WorldBBox3d(-10, -10, -10, 20, 20, 20)
            ), null, crs2154, 0, 0, 20, 20, 1, 1);

        // A simple OK test with same CRS
        final FloatMatrixProcessorParams params = new FloatMatrixProcessorParams();
        assertDoesNotThrow(() -> params.create(generation, crs2154));

        // A OK test with different CRS
        assertDoesNotThrow(() -> params.create(generation, crs4326));
    }
}
