package com.ignfab.minalac.generator.parameters.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.ignfab.minalac.generator.utils.axis.Axis;

/**
 * Parameters for a three dimensional axis.
 */
public enum AxisParams {
    /**
     * X-axis.
     */
    @JsonProperty("x")
    X(Axis.X),
    /**
     * Y-axis.
     */
    @JsonProperty("y")
    Y(Axis.Y),
    /**
     * Z-axis.
     */
    @JsonProperty("z")
    Z(Axis.Z);

    /**
     * Actual {@link Axis} associated to parameter.
     */
    public final Axis axis;

    /**
     * Creates a new axis.
     *
     * @param axis Axis associated to parameter
     */
    AxisParams(Axis axis) {
        this.axis = axis;
    }

    /**
     * {@return the corresponding {@link Axis}}
     */
    public Axis create() {
        return axis;
    }
}
