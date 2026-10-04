package fr.ign.voxatile.core.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.filters.ModelFilterOnValue;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;

/**
 * Parameters for a filter that selects models where the model value is not absent.
 */
public class ModelFilterHasValueParams extends ModelFilterParams {
    /**
     * Model value to check (required).
     */
    public ModelValueParams hasValue;

    /**
     * Creates a new {@code ModelFilterHasValueParams}.
     *
     * @param hasValue model value to check.
     */
    @ConstructorProperties("hasValue")
    public ModelFilterHasValueParams(ModelValueParams hasValue) {
        this.hasValue = hasValue;
    }

    @Override
    public void validate() {
        hasValue.validate();
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return new ModelFilterOnValue(hasValue.create(generation), v -> true);
    }
}
