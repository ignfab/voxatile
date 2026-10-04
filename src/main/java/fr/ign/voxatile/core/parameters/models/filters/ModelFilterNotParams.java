package fr.ign.voxatile.core.parameters.models.filters;

import java.beans.ConstructorProperties;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.Model;


/**
 * Parameters for "not" filter operator.
 */
public class ModelFilterNotParams extends ModelFilterParams {
    /**
     * Filter operand (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelFilterParams not;

    /**
     * Creates a new {@code ModelFilterNotParams}.
     *
     * @param not filter operand to be negated.
     */
    @ConstructorProperties({"not"})
    public ModelFilterNotParams(ModelFilterParams not) {
        this.not = not;
    }

    @Override
    public void validate() {
        not.validate();
    }

    @Override
    public Predicate<Model> create(Generation generation) {
        return not.create(generation).negate();
    }

}
