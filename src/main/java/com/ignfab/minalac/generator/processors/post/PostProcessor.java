package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * A post-processor is responsible for altering models
 * so they can comply with renderers' requirements.
 * <p>
 * Its main method is {@link #process(Model)}, which take a
 * model and return the altered model.
 * <p>
 * The type of processable models and models returned are defined
 * by the generic types {@code M1} and {@code M2} (compile-time check)
 * and must also be returned by the {@link #acceptedModelType()} and
 * {@link #processedModelType(Class)} methods to allow runtime check.
 *
 * @param <M1> The type of processable models
 * @param <M2> The type of altered models
 */
public interface PostProcessor<M1 extends Model, M2 extends Model> {
    /**
     * Returns the type of processable models.
     *
     * @return the type of processable models
     */
    Class<? super M1> acceptedModelType();

    /**
     * Returns the type of altered models.
     * It may vary depending on the model type received as an input.
     *
     * @param inputModelType the input model type
     * @return the type of altered models
     */
    Class<? extends M2> processedModelType(Class<? extends M1> inputModelType);

    /**
     * Post-processes a model and returns an altered model.
     *
     * @param model The model to post-process
     * @return The altered model (which may or may not be the input model)
     * @throws GenerationFailedException If a fatal error occurs while post-processing data
     * @throws IgnorableException If an error occurs but the model may be ignored
     */
    M2 process(M1 model) throws GenerationFailedException, IgnorableException;
}
