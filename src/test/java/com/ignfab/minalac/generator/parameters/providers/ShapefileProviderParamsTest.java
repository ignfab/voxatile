package com.ignfab.minalac.generator.parameters.providers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ShapefileProviderParamsTest {
    @Test
    public void testCreate(@TempDir File tmp) throws FactoryException, IOException {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:2154");
        Generation generation = new Generation(
            new TestingVoxelWorld(
                new WorldBBox3d(-10, -10, -10, 20, 20, 20)
            ), null, crs, 0, 0, 20, 20, 1, 1, 0);
        File file = new File(tmp, "fake.shp");
        if (!file.createNewFile())
            fail("Unable to setup test file");

        // A simple OK test
        ShapefileProviderParams params = new ShapefileProviderParams(file.getAbsolutePath());
        assertDoesNotThrow(() -> params.create(generation));

        // Missing file test
        params.filePath = "/tmp/invalid/path.shp";
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
        params.filePath = file.getAbsolutePath();

        // Wrong CRS test
        params.crsOverride = "ThisIsNotACrs";
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
    }
}
