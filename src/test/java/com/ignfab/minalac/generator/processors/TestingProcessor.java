package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
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
    public TestingModel process(String object) throws GenerationFailedException, IgnorableException {
        return new TestingModel();
    }

}
