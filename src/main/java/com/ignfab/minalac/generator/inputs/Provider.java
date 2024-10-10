package com.ignfab.minalac.generator.inputs;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;

/**
 * A provider is responsible for acquiring data.
 * <p>
 * Its main method is {@link #provide()}, which should return a
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
     * Returns the type of provided elements.
     *
     * @return the type of provided elements
     */
    Class<T> providedType();

    /**
     * Provides elements. The returned result can be iterated over
     * and then closed at the end. A typical usage may be:
     * <pre>{@code
     *  Provider<Elem> provider = ...;
     *  try (Provider.Result<Elem> result = provider.provide()) {
     *      for (Elem element : result) {
     *          // Code using element here...
     *      }
     *  }
     * }</pre>
     *
     * @return A result wrapping elements and close method
     * @throws GenerationFailedException If a fatal error occurs while fetching data
     * @throws RetryableException If an error occurs but retrying may solve the issue
     */
    Result<T> provide() throws GenerationFailedException, RetryableException;

    /**
     * A result wraps elements and a close method.
     * Once the close method has been invoked, the underlying
     * resources can be freed, and no new element will be available.
     *
     * @param <T> The type of wrapped elements
     */
    interface Result<T> extends AutoCloseable {
        /**
         * Returns the coordinate reference system of resulting data.
         *
         * @return CRS of resulting data
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

    class SimpleResult<T> implements Result<T> {
        private final CoordinateReferenceSystem crs;
        private final Iterator<T> iterator;
        private final List<? extends AutoCloseable> close;

        public SimpleResult(CoordinateReferenceSystem crs, Iterator<T> iterator, AutoCloseable... close) {
            this(crs, iterator, List.of(close));
        }

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
                    if (exception == null)
                        exception = e instanceof IgnorableException e2 ? e2 : new IgnorableException(e);
                    else
                        exception.addSuppressed(e);
                }
            }
            if (exception != null)
                throw exception;
        }
    }

    class MultiResult<T> extends SimpleResult<T> {
        private final Iterator<? extends Result<? extends T>> results;
        private Result<? extends T> current;

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
