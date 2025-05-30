package com.ignfab.minalac.generator.inputs;

import java.io.Closeable;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * A provider is responsible for acquiring data.
 * <p>
 * Its main method is {@link #provide(WorldBBox3d)}, which should return a
 * {@link Result}. A result is both iterable and closable, which
 * means it must be closed at the end of iteration, or in case
 * an error occurs and no other element will be consumed.
 * <p>
 * The type of elements provided is defined by the generic type
 * {@code T} (compile-time check) and must also be returned by
 * the {@link #providedType()} method to allow runtime check.
 *
 * @param <T> The type of provided elements
 */
public interface Provider<T> {
    /**
     * {@return the type of provided elements}
     */
    Class<T> providedType();

    /**
     * Provides elements. The returned result can be iterated over
     * and then closed at the end. A typical usage may be:
     * {@snippet lang="java" :
     * Provider<Elem> provider; // @highlight type="italic"
     *
     * try (Provider.Result<Elem> result = provider.provide()) { // @highlight substring="provider.provide()"
     *     for (Elem element : result) {
     *         // Code using element here...
     *         IO.println(element);
     *     }
     * }
     * }
     *
     * @param bbox Bbox to get elements for
     * @return A result wrapping elements and close method
     * @throws GenerationFailedException If a fatal error occurs while fetching data
     * @throws RetryableException If an error occurs but retrying may solve the issue
     */
    Result<T> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException;

    /**
     * A result wraps elements and a close method.
     * Once the close method has been invoked, the underlying
     * resources can be freed, and no new element will be available.
     *
     * @param <T> The type of wrapped elements
     */
    interface Result<T> extends Closeable {
        /**
         * {@return the coordinate reference system of resulting data}
         */
        CoordinateReferenceSystem crs();

        /**
         * Tells if more results are available for iteration.
         *
         * @return true if more results are available
         *
         * @throws RetryableException if something went wrong fetching results.
         * @throws GenerationFailedException if definitely not able to fetch results.
         */
        boolean hasNext() throws GenerationFailedException, RetryableException;

        /**
         * Returns next available result.
         *
         * @return found result
         *
         * @throws RetryableException if something went wrong fetching results.
         * @throws GenerationFailedException if definitely not able to fetch results.
         * @throws java.util.NoSuchElementException if no more result available.
         */
        T next() throws GenerationFailedException, RetryableException;
    }
}
