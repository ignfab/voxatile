package fr.ign.voxatile.core.parameters.providers;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.Provider;
import fr.ign.voxatile.core.parameters.PolymorphicParams;
import fr.ign.voxatile.core.parameters.processors.ProcessorParams;

/**
 * Represents the parameters of a type of {@link Provider}.
 */
public abstract class ProviderParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@code Provider}.
     *
     * @param generation the generation context
     * @return the resulting provider
     */
    public abstract Provider<?> create(Generation generation);

    /**
     * {@return the default processor params for this provider, if any}
     */
    public abstract ProcessorParams defaultProcessor();
}
