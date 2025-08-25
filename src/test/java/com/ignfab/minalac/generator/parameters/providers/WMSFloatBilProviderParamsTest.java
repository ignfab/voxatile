package com.ignfab.minalac.generator.parameters.providers;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class WMSFloatBilProviderParamsTest {
    @Test
    public void testCreate() throws NoSuchAuthorityCodeException, FactoryException {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:2154");
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, crs, 0, 0, 100, 100, 1, 1, 0.0, 100);

        // A simple OK test
        final WMSFloatBilProviderParams params = new WMSFloatBilProviderParams("http://toto.com", "layer1");
        assertDoesNotThrow(() -> params.create(generation));

        // Wrong CRS test
        params.crs = "ThisIsNotACrs";
        assertThrows(RuntimeException.class, () -> params.create(generation));
    }
}
