package com.ignfab.minalac.generator.inputs;

import java.util.NoSuchElementException;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class TestingProvider implements Provider<String> {
    private final CoordinateReferenceSystem crs;

    public TestingProvider(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    @Override
    public Class<String> providedType() {
        return String.class;
    }

    @Override
    public Result<String> provide(WorldBBox3d bbox) {
        return new Result<>() {
            @Override
            public CoordinateReferenceSystem crs() {
                return crs;
            }

            @Override
            public void close() {}

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public String next() {
                throw new NoSuchElementException();
            }
        };
    }
}
