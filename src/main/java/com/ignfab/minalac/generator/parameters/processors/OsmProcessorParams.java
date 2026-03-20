package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.FactoryException;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.OsmData;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.processors.OsmProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for OSM processor.
 */
public class OsmProcessorParams extends ProcessorParams {
    @Override
    public Processor<OsmData, JTSGeometryModel> create(Generation generation) {
        try {
            return new OsmProcessor(generation.makeCoordsConverter(OsmData.CRS));
        } catch (FactoryException e) {
            throw new RuntimeException("Cannot convert OSM CRS to target CRS", e);
        }
    }
}
