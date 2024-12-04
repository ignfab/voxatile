package com.ignfab.minalac.generator.parameters.renderers.values;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.renderers.values.MetadataValue;
import com.ignfab.minalac.generator.renderers.values.ModelValue;

@JsonDeserialize()
public class MetadataValueParams<T> extends ModelValueParams<T> {
    public String fromMetadata;

    @ConstructorProperties("fromMetadata")
    public MetadataValueParams(String fromMetadata) {
        this.fromMetadata = fromMetadata;
    }

    public void validate() {
        if (fromMetadata == null || fromMetadata.isBlank())
            throw new IllegalArgumentException("fromMetadata should be a non empty and blank string");
    }

    public ModelValue<T> create() {
        return new MetadataValue<T>(fromMetadata);
    }
}
