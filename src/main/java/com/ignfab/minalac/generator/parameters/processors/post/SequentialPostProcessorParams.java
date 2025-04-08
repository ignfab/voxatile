package com.ignfab.minalac.generator.parameters.processors.post;

import java.util.List;

import com.ignfab.minalac.generator.processors.post.IdentityPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.processors.post.SequentialPostProcessor;

/**
 * Parameters for {@link SequentialPostProcessor}.
 */
public class SequentialPostProcessorParams extends PostProcessorParams {
    private final List<PostProcessorParams> sequence;

    /**
     * Creates a new instance.
     * @param sequence The post-processing sequence params
     */
    public SequentialPostProcessorParams(List<PostProcessorParams> sequence) {
        this.sequence = sequence;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        for (PostProcessorParams params : sequence)
            params.validate();
    }

    @Override
    public PostProcessor<?, ?> create() {
        if (sequence.isEmpty())
            return IdentityPostProcessor.INSTANCE;
        if (sequence.size() == 1)
            return sequence.get(0).create();
        return new SequentialPostProcessor<>(sequence.stream().map(PostProcessorParams::create).toList());
    }
}
