package com.ignfab.minalac.generator.inputs;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;

public class TestingProvider implements Provider<String> {
    private CoordinateReferenceSystem crs;

    public TestingProvider(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    @Override
    public Class<String> providedType() {
        return String.class;
    }

    @Override
    public Result<String> provide() throws GenerationFailedException, RetryableException {
        return null;
    }

    @Override
    public CoordinateReferenceSystem crs() {
        return crs;
    }
}
