package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.processors.GeoToolsVectorProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for GeoTools vector processors.
 */
public class GeoToolsVectorProcessorParams extends ProcessorParams {
    @Override
    public Processor<SimpleFeature, JTSGeometryModel> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        try {
            return new GeoToolsVectorProcessor(generation.makeCoordsConverter(layerCrs));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
