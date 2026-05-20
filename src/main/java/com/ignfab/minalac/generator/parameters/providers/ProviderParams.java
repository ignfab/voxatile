package com.ignfab.minalac.generator.parameters.providers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;

/**
 * Represents the parameters of a type of {@link Provider}.
 */
public abstract class ProviderParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@code Provider}.
     *
     * @param generation the generation context
     * @return the resulting provider
     */
    public abstract Provider<?> create(Generation generation);

    /**
     * {@return the default processor params for this provider, if any}
     */
    public abstract ProcessorParams defaultProcessor();
}
