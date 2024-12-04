package com.ignfab.minalac.generator.parameters.renderers.values;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.renderers.values.FixedValue;
import com.ignfab.minalac.generator.renderers.values.ModelValue;

public class FixedValueParams<T> extends ModelValueParams<T> {
    public T value;

    @ConstructorProperties("value")
    public FixedValueParams(T value) {
        this.value = value;
    }

    public void validate() {}

    public ModelValue<T> create() {
        return new FixedValue<T>(value);
    }
}
