package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.FixedValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

public class FixedValueParams extends ModelValueParams {
    public double fixed;

    @ConstructorProperties("fixed")
    public FixedValueParams(double fixed) {
        this.fixed = fixed;
    }

    @Override
    public ModelValue create(Generation generation) {
        return new FixedValue(fixed);
    }
}
