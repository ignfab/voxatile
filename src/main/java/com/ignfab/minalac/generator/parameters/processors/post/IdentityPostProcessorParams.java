package com.ignfab.minalac.generator.parameters.processors.post;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.processors.post.IdentityPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link IdentityPostProcessor}.
 */
public class IdentityPostProcessorParams extends PostProcessorParams {
    @Override
    public PostProcessor<?, ?> create(Generation generation) {
        return IdentityPostProcessor.INSTANCE;
    }
}
