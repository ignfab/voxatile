package com.ignfab.minalac.generator.parameters.providers;

import java.io.File;
import java.io.IOException;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class GeoTiffProviderParamsTest {
    @Test
    public void testCreate(@TempDir File tmp) throws FactoryException, IOException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));
        File file = new File(tmp, "fake.tif");
        if (!file.createNewFile())
            fail("Unable to setup test file");

        // A simple OK test
        GeoTiffProviderParams params = new GeoTiffProviderParams(file.getAbsolutePath());
        assertDoesNotThrow(() -> params.create(generation));

        // Missing file test
        params.filePath = "/tmp/invalid/path.tif";
        assertThrows(IllegalArgumentException.class, () -> params.create(generation));
    }
}
