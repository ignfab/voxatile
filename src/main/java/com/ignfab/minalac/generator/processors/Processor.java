package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;

/**
 * A processor is responsible for transforming elements coming from
 * a {@link com.ignfab.minalac.generator.inputs.Provider provider}
 * into a model.
 * <p>
 * Its main method is {@link #process(Object)}, which take one
 * element and return the corresponding model.
 * <p>
 * The type of processable elements and models returned are defined
 * by the generic types {@code T} and {@code M} (compile-time check)
 * and must also be returned by the {@link #acceptedType()} and
 * {@link #modelType()} methods to allow runtime check.
 *
 * @param <T> The type of processable elements
 * @param <M> The type of created models
 */
public interface Processor<T, M extends Model> {
    /**
     * Returns the type of processable elements.
     *
     * @return the type of processable elements
     */
    Class<T> acceptedType();

    /**
     * Returns the type of created models.
     *
     * @return the type of created models
     */
    Class<M> modelType();

    /**
     * Processes an element and creates a corresponding model.
     *
     * @param object The element to process
     * @return The created model
     * @throws GenerationFailedException If a fatal error occurs while processing data
     * @throws IgnorableException If an error occurs but the element may be ignored
     */
    M process(T object) throws GenerationFailedException, IgnorableException;
}
