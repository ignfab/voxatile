package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.FallbackModelValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

/**
 * Parameters for a {@link FallbackModelValue}.
 */
public class FirstOfModelValueParams extends ModelValueParams {
    /**
     * List of values to try (required, must not be empty).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<ModelValueParams> firstOf;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param firstOf the list of values to try
     */
    @ConstructorProperties("firstOf")
    public FirstOfModelValueParams(List<ModelValueParams> firstOf) {
        this.firstOf = firstOf;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (firstOf.isEmpty())
            throw new IllegalArgumentException("There must be at least one model value");

        firstOf.forEach(ModelValueParams::validate);
    }

    @Override
    public ModelValue create(Generation generation) {
        Iterator<ModelValueParams> iterator = firstOf.iterator();

        ModelValue value = iterator.next().create(generation);

        while (iterator.hasNext())
            value = new FallbackModelValue(value, iterator.next().create(generation));

        return value;
    }
}
