package com.ignfab.minalac.generator.fetchers;

import java.io.InputStream;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public interface Fetcher {
    FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException;

    interface FetchResult {
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
        InputStream next() throws GenerationFailedException, RetryableException;
    }

    abstract class ModifyingResult implements FetchResult {
        private final FetchResult delegate;

        public ModifyingResult(FetchResult delegate) {
            this.delegate = delegate;
        }

        protected abstract InputStream modify(InputStream stream) throws GenerationFailedException, RetryableException;

        @Override
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            return delegate.hasNext();
        }

        @Override
        public InputStream next() throws GenerationFailedException, RetryableException {
            return modify(delegate.next());
        }
    }
}
