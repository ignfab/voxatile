package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.geodata.OsmData;
import com.ignfab.minalac.generator.parameters.fetchers.FetcherParams;
import com.ignfab.minalac.generator.parameters.processors.OsmProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.providers.OsmXmlProvider;
import com.ignfab.minalac.generator.providers.Provider;

/**
 * Parameters for Overpass provider.
 */
public class OsmXmlProviderParams extends ProviderParams {
    /**
     * Fetcher to get data from.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public FetcherParams fetcher;

    /**
     * Creates a new OverpassProviderParams with mandatory fields.
     *
     * @param url URL of Overpass API to use.
     * @param query Overpass query
     */
    @ConstructorProperties("fetcher")
    public OsmXmlProviderParams(FetcherParams fetcher) {
        this.fetcher = fetcher;
    }

   @Override
    public void validate() throws IllegalArgumentException {
        fetcher.validate();
    }

    @Override
    public Provider<OsmData> create(Generation generation) {
        // (Trailing ";" are removed from query)
        return new OsmXmlProvider(fetcher.create(generation));
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new OsmProcessorParams();
    }
}
