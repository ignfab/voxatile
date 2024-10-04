package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.processors.FloatMatrixProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for float matrix processors.
 */
public class FloatMatrixProcessorParams extends ProcessorParams {
    @Override
    public Processor<FloatGeographicDataMatrix2d, FloatMatrixModel> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        try {
            return new FloatMatrixProcessor(generation.makeCoordsConverter(layerCrs));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
