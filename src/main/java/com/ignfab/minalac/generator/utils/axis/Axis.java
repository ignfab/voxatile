package com.ignfab.minalac.generator.utils.axis;

/**
 * Existing axes in 3 dimensions
 */
public enum Axis {
    X, Y, Z;

    public boolean x() {
        return this == Axis.X;
    }

    public boolean y() {
        return this == Axis.Y;
    }

    public boolean z() {
        return this == Axis.Z;
    }

    /**
     * Returns a value depending on the axis.
     *
     * @param T type of values

     * @param axis to compare to
     * @param yes value to return if same
     * @param no value to return if different
     *
     * @return given {@code yes} value if {@code axis} is same as this, else returns {@code no} value.
     */
    public <T> T ifEquals(Axis axis, T yes, T no) {
        return (this == axis) ? yes : no;
    }

    /**
     * Returns a value depending on the axis.
     *
     * @param T type of values

     * @param yes value to return if axis is x-axis
     * @param no value to return if axis is not x-axis
     *
     * @return {@code yes} value if {@code axis} is x-axis, else returns {@code no} value.
     */
    public <T> T ifX(T yes, T no) {
        return ifEquals(X, yes, no);
    }

    /**
     * Returns a value depending on the axis.
     *
     * @param T type of values

     * @param yes value to return if axis is y-axis
     * @param no value to return if axis is not y-axis
     *
     * @return {@code yes} value if {@code axis} is y-axis, else returns {@code no} value.
     */
    public <T> T ifY(T yes, T no) {
        return ifEquals(Y, yes, no);
    }

    /**
     * Returns a value depending on the axis.
     *
     * @param T type of values

     * @param yes value to return if axis is z-axis
     * @param no value to return if axis is not z-axis
     *
     * @return {@code yes} value if {@code axis} is z-axis, else returns {@code no} value.
     */
    public <T> T ifZ(T yes, T no) {
        return ifEquals(Z, yes, no);
    }
}
