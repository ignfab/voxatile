package com.ignfab.minalac.generator.parameters.fetchers;

import com.ignfab.minalac.generator.fetchers.HttpFetcher;
import com.ignfab.minalac.generator.fetchers.OverpassPoolSpec;
import com.ignfab.minalac.generator.generation.Generation;

public class OverpassPoolParams {
    public MyHttpFetcherParams from;

    public void validate() {
        from.validate();
    }

    public OverpassPoolSpec create(Generation generation) {
        return new OverpassPoolSpec((HttpFetcher)from.create(generation), generation::getEnvelopeForCRS);
    }
}
