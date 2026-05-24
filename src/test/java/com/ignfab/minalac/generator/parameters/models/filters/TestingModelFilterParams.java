package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.TestingModelFilter;

/**
 * A mock filter params that can be valid or not and use TestingModelFilter as
 * filter (it says ok to models equals to model given in constructor).
 */
public class TestingModelFilterParams extends ModelFilterParams {
    private final boolean valid;
    private final Model model;

    /**
     * A valid testing model filter params.
     */
    public static final TestingModelFilterParams VALID = new TestingModelFilterParams();
    /**
     * An invalid testing model filter params.
     */
    public static final TestingModelFilterParams INVALID = new TestingModelFilterParams(false);

    public TestingModelFilterParams(boolean valid, Model model) {
        this.valid = valid;
        this.model = model;
    }

    // Simplified constructor for validate() only tests
    public TestingModelFilterParams(boolean valid) {
        this(valid, null);
    }

    // Simplified constructor for create() only tests
    public TestingModelFilterParams(Model model) {
        this(true, model);
    }

    // The yolo constructor for constructors tests
    public TestingModelFilterParams() {
        this(true, null);
    }

    @Override
    public void validate() {
        if (!valid)
            throw new IllegalArgumentException("This filter is not valid");
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new TestingModelFilter(model);
    }
}
