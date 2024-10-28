package com.ignfab.minalac.generator.parameters.processors.post;

import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link PostProcessor}.
 */
public abstract class PostProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@link PostProcessor}.
     *
     * @return the created {@link PostProcessor}
     */
    public abstract PostProcessor<?, ?> create();
}
