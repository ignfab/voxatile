package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * A post-processor is responsible for altering models
 * so they can comply with tasks' requirements.
 * <p>
 * Its main method is {@link #process(Model)}, which take a
 * model and return the altered model.
 * <p>
 * The type of processable models and models returned are
 * defined by the generic types {@code M1} and {@code M2}
 * (compile-time check) and must also be checked at runtime
 * be the {@link #checkProcessingType(Class)} method.
 *
 * @param <M1> The type of processable models
 * @param <M2> The type of altered models
 */
public interface PostProcessor<M1 extends Model, M2 extends Model> {
    /**
     * Checks that the given type can be processed by this post-processor,
     * then returns the type of altered models.
     * It may vary depending on the model type received as an input.
     *
     * @param inputModelType the input model type
     * @return the type of altered models
     * @throws IllegalArgumentException if this post-processor cannot handle the input model type
     */
    Class<? extends M2> checkProcessingType(Class<? extends Model> inputModelType) throws IllegalArgumentException;

    /**
     * Post-processes a model and returns an altered model.
     *
     * @param model The model to post-process
     * @return The altered model (which may or may not be the input model)
     * @throws GenerationFailedException If a fatal error occurs while post-processing data
     * @throws IgnorableException If an error occurs but the model may be ignored
     */
    M2 process(M1 model) throws GenerationFailedException, IgnorableException;

    /**
     * Basic post-processor implementation working on a model type.
     * The accepted model type is defined in the constructor, and the
     * post-processor is intended to return the same model type given as input.
     * @param <M> The type of models handled by this simple post-processor
     */
    abstract class Simple<M extends Model> implements PostProcessor<M, M> {
        private final Class<M> acceptedModelType;

        /**
         * Creates a new simple post-processor handling the given model type.
         * @param acceptedModelType The model type handled
         */
        public Simple(Class<M> acceptedModelType) {
            this.acceptedModelType = acceptedModelType;
        }

        @Override
        public Class<? extends M> checkProcessingType(Class<? extends Model> inputModelType) throws IllegalArgumentException {
            try {
                return inputModelType.asSubclass(acceptedModelType);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("%s cannot treat model type. Current model type = %s, Accepted model type = %s".formatted(
                    getClass().getSimpleName(),
                    inputModelType.getName(),
                    acceptedModelType.getName()
                ));
            }
        }
    }

    /**
     * Generic post-processor base, i.e. simple post-processor handling {@link Model}s.
     */
    abstract class Generic extends Simple<Model> {
        /**
         * Creates a new generic post-processor.
         */
        public Generic() {
            super(Model.class);
        }
    }
}
