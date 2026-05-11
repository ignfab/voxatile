package com.ignfab.minalac.generator.inputs;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class TestingProvider implements Provider<String> {
    private final CoordinateReferenceSystem crs;
    private final List<String> data;
    private final int failAfterElementNumber;
    private final int failsBeforeSucceed;
    private int tries = 0;

    /**
     * Creates a TestingProvider, a provider usable for tests.
     * @param crs Coordinate reference system (probably unused in most tests)
     * @param data A list of string used to create provided models (provider will provide as many model as there are strings here)
     * @param failAfterElementNumber If positive, provider will fail after returning that number of elements
     * @param failsBeforeSucceed Number of failing tries before provider succeeds, ignoring failAfterElementNumber
     */
    public TestingProvider(CoordinateReferenceSystem crs, List<String> data, int failAfterElementNumber, int failsBeforeSucceed) {
        this.crs = crs;
        this.data = data;
        this.failAfterElementNumber = failAfterElementNumber;
        this.failsBeforeSucceed = failsBeforeSucceed;
    }

    public TestingProvider(CoordinateReferenceSystem crs, List<String> data) {
        this(crs, data, -1, 0);
    }

    public TestingProvider(CoordinateReferenceSystem crs) {
        this(crs, Collections.emptyList());
    }

    public int tries() {
        return tries;
    }

    @Override
    public Class<String> providedType() {
        return String.class;
    }

    @Override
    public Result<String> provide(WorldBBox3d bbox) {
        tries++;

        return new Result<>() {
            private int index = 0;

            @Override
            public CoordinateReferenceSystem crs() {
                return crs;
            }

            @Override
            public void close() {}

            @Override
            public boolean hasNext() {
                return index < data.size();
            }

            @Override
            public String next() throws RetryableException {
                if (!hasNext())
                    throw new NoSuchElementException();
                if (tries <= failsBeforeSucceed && failAfterElementNumber >= 0 && index >= failAfterElementNumber)
                    throw new RetryableException("Failure for tests");
                index++;
                return data.get(index - 1);
            }
        };
    }


}
