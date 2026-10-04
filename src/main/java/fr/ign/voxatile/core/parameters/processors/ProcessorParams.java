package fr.ign.voxatile.core.parameters.processors;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.PolymorphicParams;
import fr.ign.voxatile.core.processors.Processor;

/**
 * Parameters for {@link Processor}.
 */
public abstract class ProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@code Processor}.
     *
     * @param generation the generation context
     * @return the resulting processor
     */
    public abstract Processor<?, ?> create(Generation generation);
}
