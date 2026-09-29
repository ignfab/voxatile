package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.OverpassFetcher;
import com.ignfab.minalac.generator.generation.Generation;

public class OverpassFetcherParams extends HttpFetcherParams {
    /**
     * Base URL for Overpass queries (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String url;

    /**
     * Overpass query, without bbox considerations (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String query;

    /**
     * Creates a new {@code OverpassFetcherParams} with mandatory fields.
     *
     * @param url Base URL for Overpass queries (including protocol, port, domain name and path but not query arguments)
     * @param query Overpass query, without bbox considerations
     */
    @ConstructorProperties({ "url", "query" })
    public OverpassFetcherParams(String url, String query) {
        this.url = url;
        this.query = query;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if (url.isBlank())
            throw new IllegalArgumentException("'url' must not be empty or blank");
        if (query.isBlank())
            throw new IllegalArgumentException("'query' must not be empty or blank");
    }

    @Override
    public Fetcher create(Generation generation) {
        return new OverpassFetcher(createHttpInit(), url, query.replaceAll(";+$", ""), generation::getEnvelopeForCRS);
    }
}
