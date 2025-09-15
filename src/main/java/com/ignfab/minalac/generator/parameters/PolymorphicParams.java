package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for all polymorphic parameters with validation.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class PolymorphicParams implements Params {
    /**
     * This field is required.
     * It is not deserialized but used to find out the class of parameters to deserialize.
     */
    public String type;
}
