package com.ignfab.minalac.generator.inputs;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
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
     * } catch (...) {
     *     // Handle exceptions here...
     * }
     * }
     *
     * @param bbox limits of the area to provide elements from
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
    interface Result<T> extends AutoCloseable {
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

        @Override
        void close() throws IgnorableException;
    }

    /**
     * Result implementation based on an iterator, and elements to close (optional).
     * @param <T> The type of wrapped elements
     */
    class SimpleResult<T> implements Result<T> {
        private final CoordinateReferenceSystem crs;
        private final Iterator<T> iterator;
        private final List<? extends AutoCloseable> close;

        /**
         * Creates a new simple result.
         * @param crs the CRS of resulting data
         * @param iterator the iterator to get data from
         * @param close optional elements to close at the end
         */
        public SimpleResult(CoordinateReferenceSystem crs, Iterator<T> iterator, AutoCloseable... close) {
            this(crs, iterator, List.of(close));
        }

        /**
         * Creates a new simple result.
         * @param crs the CRS of resulting data
         * @param iterator the iterator to get data from
         * @param close list of elements to close at the end
         */
        public SimpleResult(CoordinateReferenceSystem crs, Iterator<T> iterator, List<? extends AutoCloseable> close) {
            this.crs = crs;
            this.iterator = iterator;
            this.close = close;
        }

        @Override
        public CoordinateReferenceSystem crs() {
            return crs;
        }

        @Override
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            return iterator.hasNext();
        }

        @Override
        public T next() throws GenerationFailedException, RetryableException {
            return iterator.next();
        }

        @Override
        public void close() throws IgnorableException {
            IgnorableException exception = null;
            for (AutoCloseable closeable : close) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // Save first exception, suppress others
                    if (exception == null)
                        exception = e instanceof IgnorableException ie ? ie : new IgnorableException(e);
                    else
                        exception.addSuppressed(e);
                }
            }
            if (exception != null)
                throw exception;
        }
    }

    /**
     * Result implementation based on multiple results.
     * Each result is getting iterated over in order, and they all get closed at the end.
     * @param <T> The type of wrapped elements
     */
    class MultiResult<T> extends SimpleResult<T> {
        private final Iterator<? extends Result<? extends T>> results;
        private Result<? extends T> current;

        /**
         * Creates a new multi-result.
         * @param crs the CRS of resulting data
         * @param results the results to iterator over
         * @throws GenerationFailedException propagated if thrown by an underlying result
         * @throws RetryableException propagated if thrown by an underlying result
         */
        public MultiResult(CoordinateReferenceSystem crs, List<? extends Result<? extends T>> results) throws GenerationFailedException, RetryableException {
            super(crs, null, results);
            this.results = results.iterator();
            moveOn();
        }

        private void moveOn() throws GenerationFailedException, RetryableException {
            while ((current == null || !current.hasNext()) && results.hasNext())
                current = results.next();
        }

        @Override
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            return current != null && current.hasNext();
        }

        @Override
        public T next() throws GenerationFailedException, RetryableException {
            if (current == null || !current.hasNext())
                throw new NoSuchElementException();
            T element = current.next();
            moveOn();
            return element;
        }
    }
}
