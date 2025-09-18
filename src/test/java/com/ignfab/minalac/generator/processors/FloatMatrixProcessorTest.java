package com.ignfab.minalac.generator.processors;

import java.util.concurrent.atomic.AtomicBoolean;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

import static org.junit.jupiter.api.Assertions.*;

public class FloatMatrixProcessorTest {
    private static final MapToWorldConverter IDENTITY_CONVERTER = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());

    @Test
    public void test() throws FactoryException, TransformException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
        AtomicBoolean initialized = new AtomicBoolean(false);
        FloatMatrixProcessor processor = new FloatMatrixProcessor(crs -> {
            initialized.set(crs == crs2154);
            return IDENTITY_CONVERTER;
        });
        assertDoesNotThrow(() -> processor.initialize(crs2154)); // layerCrs is only validated but not actually used by the converter provider we gave above
        assertTrue(initialized.get(), "processor initialized with given CRS");

        // Validate capabilities of processor
        assertTrue(processor.acceptedType().isAssignableFrom(FloatGeographicDataMatrix2d.class), "It accepts FloatGeographicDataMatrix2d");
        assertTrue(FloatMatrixModel.class.isAssignableFrom(processor.modelType()), "It produces a FloatMatrixModel");
    }
}
