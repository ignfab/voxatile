package com.ignfab.minalac.generator.parameters.processors;

import org.citygml4j.core.model.core.AbstractCityObject;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.CityBuildingModel;
import com.ignfab.minalac.generator.processors.CityJSONBuildingProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for CityJSON building processors.
 */
public class CityJSONBuildingProcessorParams extends ProcessorParams {
    @Override
    public Processor<AbstractCityObject, CityBuildingModel> create(Generation generation) {
        return new CityJSONBuildingProcessor(generation::makeCoordsConverter);
    }
}
