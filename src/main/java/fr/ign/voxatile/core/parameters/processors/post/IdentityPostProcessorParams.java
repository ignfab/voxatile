package fr.ign.voxatile.core.parameters.processors.post;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.processors.post.IdentityPostProcessor;
import fr.ign.voxatile.core.processors.post.PostProcessor;

/**
 * Parameters for {@link IdentityPostProcessor}.
 */
public class IdentityPostProcessorParams extends PostProcessorParams {
    @Override
    public PostProcessor<?, ?> create(Generation generation) {
        return IdentityPostProcessor.INSTANCE;
    }
}
