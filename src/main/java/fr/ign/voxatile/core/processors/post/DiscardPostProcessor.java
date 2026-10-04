package fr.ign.voxatile.core.processors.post;

import fr.ign.voxatile.core.models.Model;

/**
 * Post-processor discarding everything.
 * Can be used for example inside a {@link ConditionalPostProcessor} to discard matching models.
 */
public final class DiscardPostProcessor extends PostProcessor.Generic {
    /**
     * Singleton instance.
     */
    public static final DiscardPostProcessor INSTANCE = new DiscardPostProcessor();

    /**
     * @see #INSTANCE
     */
    private DiscardPostProcessor() {}

    @Override
    public Model process(Model model) {
        return null;
    }
}
