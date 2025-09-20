package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.InverseModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Parameters for {@link InverseModelValue}.
 */
public class InverseModelValueParams extends ModelValueParams {
    /**
     * Model value to compute inverse from (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams inverse;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param inverse the model value to use
     */
    @ConstructorProperties("inverse")
    public InverseModelValueParams(ModelValueParams inverse) {
        this.inverse = inverse;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        inverse.validate();
    }

    @Override
    public ModelValue create(Generation generation) {
        return new InverseModelValue(inverse.create(generation));
    }
}
