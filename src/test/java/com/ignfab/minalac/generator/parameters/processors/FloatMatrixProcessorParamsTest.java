package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FloatMatrixProcessorParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");

        Generation generation = new Generation(
            new TestingVoxelWorld(
                new WorldBBox3d(-10, -10, -10, 20, 20, 20)
            ), null, crs2154, 0, 0, 20, 20, 1, 1, 0.0);

        // A simple OK test
        final FloatMatrixProcessorParams params = new FloatMatrixProcessorParams();
        assertDoesNotThrow(() -> params.create(generation));
    }
}
