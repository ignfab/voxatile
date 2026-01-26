package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

// TODO Requires testing
public abstract class ConvolutionHeightmapOperator implements UnaryHeightmapOperator {
    protected final int halfSize;

    /**
     * Creates a new {@code ConvolutionHeightmapOperator}.
     *
     * @param halfSize the half size of the kernel for convolution
     */
    public ConvolutionHeightmapOperator(int halfSize) {
        this.halfSize = halfSize;
    }

    protected abstract double kernel(int i, int j, ReadableHeightmap heightmap, int x, int y, int val);

    @Override
    public int compute(int x, int y, ReadableHeightmap operand) {
        double value = 0;
        double total = 0;
        for (int i = -halfSize; i <= halfSize; i++) {
            for (int j = -halfSize; j <= halfSize; j++) {
                int xi = x + i;
                int yj = y + j;
                if (operand.bbox().contains(xi, yj)) {
                    int v = operand.get(xi, yj);
                    double k = kernel(i, j, operand, x, y, v);
                    value += v * k;
                    total += k;
                }
            }
        }
        return (int) Math.round(value / total);
    }

    public static class Simple extends ConvolutionHeightmapOperator {
        private final double[][] kernel;

        /**
         * Creates a new simple {@code ConvolutionHeightmapOperator}.
         *
         * @param kernel the desired kernel for convolution
         */
        public Simple(double[][] kernel) {
            super((kernel.length - 1) / 2);
            this.kernel = kernel;
        }

        @Override
        protected double kernel(int i, int j, ReadableHeightmap heightmap, int x, int y, int val) {
            return kernel[i + halfSize][j + halfSize];
        }
    }

    public static class Bilateral extends Simple {
        private final double range;

        /**
         * Creates a new bilateral filtering {@code ConvolutionHeightmapOperator}.
         *
         * @param kernel the desired kernel for convolution
         * @param range the minimum amplitude of an edge
         */
        public Bilateral(double[][] kernel, double range) {
            super(kernel);
            this.range = range;
        }

        @Override
        protected double kernel(int i, int j, ReadableHeightmap heightmap, int x, int y, int val) {
            return super.kernel(i, j, heightmap, x, y, val) * gaussian(Math.abs(heightmap.get(x, y) - val), range * range);
        }

        private static double gaussian(double x, double sigma2) {
            return 1 / (2 * Math.PI * sigma2) * Math.exp(-x * x / (2 * sigma2));
        }
    }
}
