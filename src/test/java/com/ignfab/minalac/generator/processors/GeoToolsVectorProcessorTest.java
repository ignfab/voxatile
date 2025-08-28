package com.ignfab.minalac.generator.processors;

import java.util.concurrent.atomic.AtomicBoolean;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.TestingConverter;

import static org.junit.jupiter.api.Assertions.*;

public class GeoToolsVectorProcessorTest {

    @Test
    public void test() throws SchemaException, FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
        AtomicBoolean initialized = new AtomicBoolean(false);
        GeoToolsVectorProcessor processor = new GeoToolsVectorProcessor(crs -> {
            initialized.set(crs == crs2154);
            return TestingConverter.IDENTITY;
        });
        assertDoesNotThrow(() -> processor.initialize(crs2154)); // layerCrs is only validated but not actually used by the converter provider we gave above
        assertTrue(initialized.get(), "processor initialized with given CRS");

        // Validate capabilities of processor
        assertTrue(processor.acceptedType().isAssignableFrom(SimpleFeature.class), "processor accepts SimpleFeature");
        assertTrue(JTSGeometryModel.class.isAssignableFrom(processor.modelType()), "processor produces JTSGeometryModel");

        // Construct dummy feature
        SimpleFeatureType featureType = DataUtilities.createType("FLAG", "id:Integer,name:String,*geom:Geometry:4326");
        SimpleFeature feature = DataUtilities.createFeature(featureType, "1|Dummy feature|POINT (1 2)");

        // Validate processing
        JTSGeometryModel model = assertDoesNotThrow(() -> processor.process(feature));

        // Validate metadata
        assertEquals(1, (int) model.getMetadata("id"));
        assertEquals("Dummy feature", model.getMetadata("name"));
        assertTrue(model.hasMetadata("geom"));

        // Test 2d conversion (Could be improved checking shape contains only given point)
        assertDoesNotThrow(() -> model.toShape2d());

        // Test 3d conversion (Could be improved checking shape contains only given point)
        assertDoesNotThrow(() -> model.toShape3d());
    }
}
