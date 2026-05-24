package com.ignfab.minalac.generator.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;

/**
 * Parameters for combining filters with "or" operator.
 */
public class ModelFilterOrParams extends ModelFilterParams {
    /**
     * List of filters operands (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<ModelFilterParams> or;

    /**
     * Creates a new {@code ModelFilterOrParams}.
     *
     * @param or List of filters operands to be combined.
     */
    @ConstructorProperties({"or"})
    public ModelFilterOrParams(List<ModelFilterParams> or) {
        this.or = or;
    }

    @Override
    public void validate() {
        if (or.isEmpty())
            throw new IllegalArgumentException("There must be at least one sub-filter");

        for (ModelFilterParams filter : or) {
            filter.validate();
        }
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        Iterator<ModelFilterParams> iterator = or.iterator();
        Predicate<Model> predicate = iterator.next().create(generation);

        while (iterator.hasNext())
            predicate = predicate.or(iterator.next().create(generation));

        return predicate;
    }

}
