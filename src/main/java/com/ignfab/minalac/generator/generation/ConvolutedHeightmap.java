package com.ignfab.minalac.generator.generation;

public class ConvolutedHeightmap implements ReadableHeightmap {

    private ReadableHeightmap heightmap;
    private Kernel kernel;

    public ConvolutedHeightmap(ReadableHeightmap heightmap, double a, int[] values, int kernelSize) {
        this.heightmap = heightmap;
        kernel = new Kernel(a, values, kernelSize);
    }

    public ConvolutedHeightmap(ReadableHeightmap heightmap) {
        this.heightmap = heightmap;
        kernel = new Kernel((double) 1 / 9.0, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 3);
    }

    @Override
    public int get(int x, int y) {
        int offset = kernel.size / 2;
        int sum = 0;
        for (int i = -offset; i <= offset; i++) {
            for (int j = -offset; j <= offset; j++) {
                int val = 0;
                // TODO-PR : temporary
                // Can be a getDefault()
                try {
                    val = heightmap.get(x + i, x + j);
                } catch (Exception ignored) {}
                sum = sum + val * kernel.get(i, j);
            }
        }
        return (int) Math.floor(kernel.a * sum);
    }

    public static void main(String[] args) {
        ReadableHeightmap map = new Heightmap(0, 0, 3, 3, 27);
        System.out.println(map.get(1, 1));
        ReadableHeightmap convo = new ConvolutedHeightmap(map);
        System.out.println(convo.get(0, 0));
    }

    private static class Kernel {
        double a;
        int[] values;
        int size;

        Kernel(double a, int[] values, int size) {
            if (values.length != size * size)
                throw new IllegalArgumentException("size mismatch");
            if (size % 2 == 0)
                throw new IllegalArgumentException("not odd");
            this.a = a;
            this.values = values;
            this.size = size;
        }

        int get(int x, int y) {
            return values[(y + (size / 2)) * size + (x + (size / 2))];
        }
    }
}
