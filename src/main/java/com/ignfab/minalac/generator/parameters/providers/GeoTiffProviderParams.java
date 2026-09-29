package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.geodata.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.parameters.fetchers.FetcherParams;
import com.ignfab.minalac.generator.parameters.processors.FloatMatrixProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.providers.GeoTiffProvider;
import com.ignfab.minalac.generator.providers.Provider;

/**
 * Parameters for {@link GeoTiffProvider}.
 */
public class GeoTiffProviderParams extends ProviderParams {
    /**
     * Fetcher to get data from (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public FetcherParams fetcher;

    /**
     * Creates a new {@code GeoTiffProviderParams} with mandatory fields.
     *
     * @param fetcher Fetcher to get data from
     */
    @ConstructorProperties("fetcher")
    public GeoTiffProviderParams(FetcherParams fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        fetcher.validate();
    }

    @Override
    public Provider<FloatGeographicDataMatrix2d> create(Generation generation) {
        return new GeoTiffProvider(fetcher.create(generation), generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new FloatMatrixProcessorParams();
    }
}
