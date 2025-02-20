package com.ignfab.minalac.generator.processors;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.models.TestingModel;

public class TestingProcessor implements Processor<String, TestingModel> {

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
        return new TestingModel();
    }

}
