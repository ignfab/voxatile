package com.ignfab.minalac.generator.parameters.processors.post;

import com.ignfab.minalac.generator.processors.post.DiscardPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link DiscardPostProcessor}.
 */
public class DiscardPostProcessorParams extends PostProcessorParams {
    @Override
    public PostProcessor<?, ?> create() {
        return DiscardPostProcessor.INSTANCE;
    }
}
