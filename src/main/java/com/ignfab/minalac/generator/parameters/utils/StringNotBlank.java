package com.ignfab.minalac.generator.parameters.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.parameters.Params;

@JsonDeserialize(using = StringNotBlank.Deserializer.class)
public class StringNotBlank implements Params {
    private final String fieldName;
    private final String value;

    public StringNotBlank(String value) {
        this(null, value);
        validate(); // Force validation here when constructed manually
    }

    private StringNotBlank(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(fieldName == null ? "The field cannot be blank" : "The field '%s' cannot be blank".formatted(fieldName));
    }

    public String create() {
        return value;
    }

    static class Deserializer extends JsonDeserializer<StringNotBlank> {
        @Override
        public StringNotBlank deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return new StringNotBlank(parser.currentName(), parser.readValueAs(String.class));
        }
    }
}
