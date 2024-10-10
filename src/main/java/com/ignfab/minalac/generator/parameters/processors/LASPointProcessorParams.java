package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASPointModel;
import com.ignfab.minalac.generator.processors.LASPointProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for LAS point processors.
 */
public class LASPointProcessorParams extends ProcessorParams {
    @Override
    public Processor<LASPointAndHeader, LASPointModel> create(Generation generation) {
        return new LASPointProcessor(generation::makeCoordsConverter);
    }
}
