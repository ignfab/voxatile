package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASPointModel;
import com.ignfab.minalac.generator.processors.LASPointProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * Parameters for LAS point processors.
 */
public class LASPointProcessorParams extends ProcessorParams {
    @Override
    public Processor<LASPointAndHeader, LASPointModel> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        try {
            return new LASPointProcessor(generation.makeCoordsConverter(layerCrs));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
