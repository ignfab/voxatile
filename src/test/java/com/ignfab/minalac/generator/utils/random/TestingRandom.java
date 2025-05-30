package com.ignfab.minalac.generator.utils.random;

/**
 * A testing class faking Random and returning the same value last set by setNextXXX() methods.
 */
public class TestingRandom extends Random {

    private boolean nextBoolean = false;
    private double nextDouble = 0d;
    private float nextFloat = 0f;
    private int nextInt = 0;
    private long nextLong = 0L;

    public TestingRandom(Seed seed) {
        super(seed);
    }

    @Override
    public boolean nextBoolean() {
        return nextBoolean;
    }

    /**
     * Sets next boolean values.
     *
     * @param value value to set
     */
    public void setNextBoolean(boolean value) {
        nextBoolean = value;
    }

    @Override
    public double nextDouble() {
        return nextDouble;
    }

    /**
     * Sets next double values.
     *
     * @param value value to set
     */
    public void setNextDouble(double value) {
        nextDouble = value;
    }

    @Override
    public float nextFloat() {
        return nextFloat;
    }

    /**
     * Sets next float values.
     *
     * @param value value to set
     */
    public void setNextFloat(float value) {
        nextFloat = value;
    }

    @Override
    public int nextInt() {
        return nextInt;
    }

    /**
     * Sets next int values.
     *
     * @param value value to set
     */
    public void setNextInt(int value) {
        nextInt = value;
    }

    @Override
    public long nextLong() {
        return nextLong;
    }

    /**
     * Sets next long values.
     *
     * @param value value to set
     */
    public void setNextLong(long value) {
        nextLong = value;
    }
}
