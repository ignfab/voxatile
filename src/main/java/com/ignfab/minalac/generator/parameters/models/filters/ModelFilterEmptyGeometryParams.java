package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.filters.ModelFilterEmptyGeometry;

/**
 * Parameters for a {@link ModelFilterEmptyGeometry}.
 */
public class ModelFilterEmptyGeometryParams extends ModelFilterParams {
    /**
     * Unused unique property to disambiguate from other filters because deduction is used...
     */
    public Object emptyGeometry;

    @Override
    public Predicate<Model> create() {
        return ModelFilterEmptyGeometry.INSTANCE;
    }
}
