package com.ignfab.minalac.generator.processors;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.models.TestingModel;

public class TestingProcessor implements Processor<String, TestingModel> {
    private int processed = 0;

    public int processed() {
        return processed;
    }

    @Override
    public Class<String> acceptedType() {
        return String.class;
    }

    @Override
    public Class<TestingModel> modelType() {
        return TestingModel.class;
    }

    @Override
    public void initialize(CoordinateReferenceSystem layerCrs) {}

    @Override
    public TestingModel process(String object) {
        processed++;
        return new TestingModel();
    }

}
