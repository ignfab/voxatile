package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASMergedModel;
import com.ignfab.minalac.generator.processors.LASPointMergedModelProcessor;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for LAS point merged model processors.
 */
public class LASPointMergedModelProcessorParams extends ProcessorParams {
    @Override
    public Processor<LASPointAndHeader, LASMergedModel> create(Generation generation) {
        return new LASPointMergedModelProcessor(generation::makeCoordsConverter);
    }
}
