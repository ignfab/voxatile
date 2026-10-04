package fr.ign.voxatile.core.parameters.models.filters;

import java.util.function.Predicate;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.filters.ModelFilterEmptyGeometry;

/**
 * Parameters for a {@link ModelFilterEmptyGeometry}.
 */
public class ModelFilterEmptyGeometryParams extends ModelFilterParams {
    /**
     * Unused unique property to disambiguate from other filters because deduction is used...
     */
    public Object emptyGeometry;

    @Override
    public Predicate<Model> create(Generation generation) {
        return ModelFilterEmptyGeometry.INSTANCE;
    }
}
