package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.FixedValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Parameters for a {@link FixedValue}.
 */
public class FixedValueParams extends ModelValueParams {
    /**
     * The fixed value (required).
     */
    public double fixed;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param fixed the fixed model value
     */
    @ConstructorProperties("fixed")
    public FixedValueParams(double fixed) {
        this.fixed = fixed;
    }

    @Override
    public ModelValue create(Generation generation) {
        return new FixedValue(fixed);
    }
}
