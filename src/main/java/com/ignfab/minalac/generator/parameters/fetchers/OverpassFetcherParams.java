package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.OverpassFetcher;
import com.ignfab.minalac.generator.generation.Generation;

public class OverpassFetcherParams extends FetcherParams {
    /**
     * Overpass query, without bbox considerations (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String query;

    /**
     * Creates a new {@code OverpassFetcherParams} with mandatory fields.
     *
     * @param query Overpass query, without bbox considerations
     */
    @ConstructorProperties({"query" })
    public OverpassFetcherParams(String query) {
        this.query = query;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (query.isBlank())
            throw new IllegalArgumentException("'query' must not be empty or blank");
    }

    @Override
    public Fetcher create(Generation generation) {
        return new OverpassFetcher(generation.overpassPoolSpec, query.replaceAll(";+$", ""), generation::getEnvelopeForCRS);
    }
}
