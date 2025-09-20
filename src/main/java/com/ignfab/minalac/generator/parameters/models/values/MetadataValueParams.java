package com.ignfab.minalac.generator.parameters.models.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.values.MetadataValue;
import com.ignfab.minalac.generator.models.values.ModelValue;

public class MetadataValueParams extends ModelValueParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    @ConstructorProperties("metadata")
    public MetadataValueParams(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public void validate() {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public ModelValue create(Generation generation) {
        return new MetadataValue(metadata);
    }
}
