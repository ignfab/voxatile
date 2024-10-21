package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.CityBuildingModel;
import com.ignfab.minalac.generator.processors.CityJSONBuildingProcessor;
import com.ignfab.minalac.generator.processors.Processor;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * Parameters for CityJSON building processors.
 */
public class CityJSONBuildingProcessorParams extends ProcessorParams {
    @Override
    public Processor<AbstractCityObject, CityBuildingModel> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        try {
            return new CityJSONBuildingProcessor(generation.makeCoordsConverter(layerCrs));
        } catch (FactoryException e) {
            throw new IllegalArgumentException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
