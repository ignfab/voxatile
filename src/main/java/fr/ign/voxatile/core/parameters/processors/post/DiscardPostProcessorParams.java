package fr.ign.voxatile.core.parameters.processors.post;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.processors.post.DiscardPostProcessor;
import fr.ign.voxatile.core.processors.post.PostProcessor;

/**
 * Parameters for {@link DiscardPostProcessor}.
 */
public class DiscardPostProcessorParams extends PostProcessorParams {
    @Override
    public PostProcessor<?, ?> create(Generation generation) {
        return DiscardPostProcessor.INSTANCE;
    }
}
