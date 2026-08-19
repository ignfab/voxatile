package com.ignfab.minalac.generator.parameters.fetchers;

import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;

/**
 * Represents the parameters of a type of {@link Fetcher}.
 */
public abstract class FetcherParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@link Fetcher}.
     *
     * @param generation the generation context
     * @return the resulting fetcher
     */
    public abstract Fetcher create(Generation generation);
}
