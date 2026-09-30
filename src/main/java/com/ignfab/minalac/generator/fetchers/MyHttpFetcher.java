package com.ignfab.minalac.generator.fetchers;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class MyHttpFetcher extends HttpFetcher {

    public MyHttpFetcher(HttpInit init, ParameterizedURL baseURL) {
        super(init, baseURL);
    }

    @Override
    public FetchResult fetch(WorldBBox3d bbox) throws RetryableException, GenerationFailedException {
        throw new UnsupportedOperationException("Unimplemented method 'fetch'");
    }
}
