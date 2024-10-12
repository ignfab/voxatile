package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.ImageGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.processors.IntegerMatrixProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * Parameters for integer matrix processors.
 */
public class IntegerMatrixProcessorParams extends ProcessorParams {
    @Override
    public Processor<ImageGeographicDataMatrix2d, IntegerMatrixModel> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        try {
            return new IntegerMatrixProcessor(generation.makeCoordsConverter(layerCrs));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
