package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for all polymorphic parameters with validation.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class PolymorphicParams {
    /**
     * This field is required.
     * It is not deserialized but used to find out the class of parameters to deserialize.
     */
    public String type;

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {}
}
