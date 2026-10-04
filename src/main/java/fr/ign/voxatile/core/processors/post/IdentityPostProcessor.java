package fr.ign.voxatile.core.processors.post;

import fr.ign.voxatile.core.models.Model;

/**
 * Post-processor doing nothing. Can be convenient as a default value when not needed.
 */
public final class IdentityPostProcessor extends PostProcessor.Generic {
    /**
     * Singleton instance.
     */
    public static final IdentityPostProcessor INSTANCE = new IdentityPostProcessor();

    /**
     * @see #INSTANCE
     */
    private IdentityPostProcessor() {}

    @Override
    public Model process(Model model) {
        return model;
    }
}
