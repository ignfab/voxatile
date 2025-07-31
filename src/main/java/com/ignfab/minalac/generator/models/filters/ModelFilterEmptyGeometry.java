package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.models.Model;

/**
 * A model filter testing emptiness of geometry.
 * Only {@link JTSGeometryModel}s can be tested by this filter (otherwise returns {@code false}).
 */
public final class ModelFilterEmptyGeometry implements Predicate<Model> {
    /**
     * Singleton instance.
     */
    public static final ModelFilterEmptyGeometry INSTANCE = new ModelFilterEmptyGeometry();

    /**
     * @see #INSTANCE
     */
    private ModelFilterEmptyGeometry() {}

    @Override
    public boolean test(Model model) {
        return model instanceof JTSGeometryModel geoModel && geoModel.getGeometry().isEmpty();
    }
}
