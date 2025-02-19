package com.ignfab.minalac.generator.parameters.processors;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Parameters for {@link Processor}.
 */
public abstract class ProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@code Processor}.
     *
     * @param generation the generation context
     * @return the resulting processor
     */
    public abstract Processor<?, ?> create(Generation generation);
}
