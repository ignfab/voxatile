package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;
/**
 * A mock filter that responds true if tested model is equals to model given to constructor.
 */
public class TestingModelFilter implements Predicate<Model> {
    private final Model model;

    public TestingModelFilter(Model model) {
        this.model = model;
    }

    @Override
    public boolean test(Model model) {
        return this.model == model;
    }
}
