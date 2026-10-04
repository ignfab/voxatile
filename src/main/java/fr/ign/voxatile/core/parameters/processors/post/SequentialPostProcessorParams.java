package fr.ign.voxatile.core.parameters.processors.post;

import java.util.List;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.processors.post.IdentityPostProcessor;
import fr.ign.voxatile.core.processors.post.PostProcessor;
import fr.ign.voxatile.core.processors.post.SequentialPostProcessor;

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
    public PostProcessor<?, ?> create(Generation generation) {
        if (sequence.isEmpty())
            return IdentityPostProcessor.INSTANCE;
        if (sequence.size() == 1)
            return sequence.get(0).create(generation);
        return new SequentialPostProcessor<>(sequence.stream().map(params -> params.create(generation)).toList());
    }
}
