package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.processors.GeoToolsVectorProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * Parameters for GeoTools vector processors.
 */
public class GeoToolsVectorProcessorParams extends ProcessorParams {
    @Override
    public Processor<SimpleFeature, JTSGeometryModel> create(Generation generation) {
        return new GeoToolsVectorProcessor(generation::makeCoordsConverter);
    }
}
