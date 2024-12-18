package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.TestingModelFilter;

/**
 * A mock filter params that can be valid or not and use TestingModelFilter as
 * filter (it says ok to models equals to model given in constructor).
 */
public class TestingModelFilterParams extends ModelFilterParams {
    boolean valid;
    Model model;

    public TestingModelFilterParams(boolean valid, Model model) {
        this.valid = valid;
        this.model = model;
    }

    // Simplified constructor for validate() only tests
    public TestingModelFilterParams(boolean valid) {
        this.valid = valid;
        this.model = null;
    }

    // Simplified constructor for create() only tests
    public TestingModelFilterParams(Model model) {
        this.valid = true;
        this.model = model;
    }

    // The yolo constructor for constructors tests
    public TestingModelFilterParams() {
        this.valid = true;
        this.model = null;
    }

    @Override
    public void validate() {
        if (!valid)
            throw new IllegalArgumentException("This filter is not valid");
    }

    @Override
    public Predicate<Model> create() {
        return new TestingModelFilter(model);
    }
}
