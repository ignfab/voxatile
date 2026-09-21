package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.feature.simple.SimpleFeature;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.fetchers.FetcherParams;
import com.ignfab.minalac.generator.parameters.processors.GeoToolsVectorProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.providers.GML3_1_Provider;
import com.ignfab.minalac.generator.providers.Provider;

/**
 * Parameters for {@link GML3_1_Provider}.
 */
public class GMLProviderParams extends ProviderParams {
    /**
     * Fetcher to get data from.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public FetcherParams fetcher;

    /**
     * Creates a new {@code GMLProviderParams} with mandatory fields.
     *
     * @param fetcher fetcher to get data from
     */
    @ConstructorProperties("fetcher")
    public GMLProviderParams(FetcherParams fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        fetcher.validate();
    }

    @Override
    public Provider<SimpleFeature> create(Generation generation) {
        return new GML3_1_Provider(fetcher.create(generation));
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new GeoToolsVectorProcessorParams();
    }
}
