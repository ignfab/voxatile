package com.ignfab.minalac.generator.parameters.fetchers;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.fetchers.MyHttpFetcher;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;

public class MyHttpFetcherParams extends HttpFetcherParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public String url;

    @Override
    public Fetcher create(Generation generation) {
        return new MyHttpFetcher(createHttpInit(), ParameterizedURL.base(url).build());
    }
}
