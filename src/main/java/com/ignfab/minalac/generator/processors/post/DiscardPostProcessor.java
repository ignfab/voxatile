package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.models.Model;

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
