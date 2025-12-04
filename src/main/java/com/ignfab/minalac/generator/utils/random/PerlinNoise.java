package com.ignfab.minalac.generator.utils.random;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

/**
 * Perlin noise computation.
 */
public final class PerlinNoise {
    private PerlinNoise() {
        throw new UnsupportedOperationException();
    }

    private static double blend(double t) {
        return ((6 * t - 15) * t + 10) * t * t * t;
    }

    private static double interpolate(double start, double end, double progress) {
        return start * (1 - progress) + end * progress;
    }

    private static double gradientRamp(Random random, int i, int j, double u, double v) {
        random.setSeed(i, j);
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        return (u * x + v * y) / sqrt(x * x + y * y);
    }

    private static double compute2d(Random random, double x, double y) {
        int i = (int) floor(x);
        int j = (int) floor(y);
        double u = x - i;
        double v = y - j;

        double n00 = gradientRamp(random, i, j, u, v);
        double n10 = gradientRamp(random, i + 1, j, u - 1, v);
        double n01 = gradientRamp(random, i, j + 1, u, v - 1);
        double n11 = gradientRamp(random, i + 1, j + 1, u - 1, v - 1);

        double ub = blend(u);
        return interpolate(interpolate(n00, n10, ub), interpolate(n01, n11, ub), blend(v));
    }

    /**
     * Computes the normalized 2d perlin noise on a grid with cell of given size.
     * @param random random number generator
     * @param x x-coordinate
     * @param y y-coordinate
     * @param size grid cell size
     * @return random perlin noise value normalized between 0 and 1
     */
    public static double get2d(Random random, int x, int y, double size) {
        double perlin = compute2d(random, x * size, y * size);
        return perlin / 1.4142135623730951 + 0.5; // sqrt(2)
    }

    private static double gradientRamp(Random random, int i, int j, int k, double u, double v, double w) {
        random.setSeed(i, j, k);
        double x = random.nextGaussian();
        double y = random.nextGaussian();
        double z = random.nextGaussian();
        return (u * x + v * y + w * z) / sqrt(x * x + y * y + z * z);
    }

    private static double compute3d(Random random, double x, double y, double z) {
        int i = (int) floor(x);
        int j = (int) floor(y);
        int k = (int) floor(z);
        double u = x - i;
        double v = y - j;
        double w = z - k;

        double n000 = gradientRamp(random, i, j, k, u, v, w);
        double n100 = gradientRamp(random, i + 1, j, k, u - 1, v, w);
        double n010 = gradientRamp(random, i, j + 1, k, u, v - 1, w);
        double n110 = gradientRamp(random, i + 1, j + 1, k, u - 1, v - 1, w);
        double n001 = gradientRamp(random, i, j, k + 1, u, v, w - 1);
        double n101 = gradientRamp(random, i + 1, j, k + 1, u - 1, v, w - 1);
        double n011 = gradientRamp(random, i, j + 1, k + 1, u, v - 1, w - 1);
        double n111 = gradientRamp(random, i + 1, j + 1, k + 1, u - 1, v - 1, w - 1);

        double ub = blend(u);
        double vb = blend(v);
        return interpolate(
            interpolate(interpolate(n000, n100, ub), interpolate(n010, n110, ub), vb),
            interpolate(interpolate(n001, n101, ub), interpolate(n011, n111, ub), vb),
            blend(w)
        );
    }

    /**
     * Computes the normalized 3d perlin noise on a grid with cell of given size.
     * @param random random number generator
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param size grid cell size
     * @return random perlin noise value normalized between 0 and 1
     */
    public static double get3d(Random random, int x, int y, int z, double size) {
        double perlin = compute3d(random, x * size, y * size, z * size);
        return perlin / 1.7320508075688772 + 0.5; // sqrt(3)
    }
}
