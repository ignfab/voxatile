package com.ignfab.minalac.generator.models;

import org.geotools.api.referencing.operation.TransformException;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.world2d.chunk.EmptyChunk2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;

/**
 * A model based on a matrix of floats (usually a heightmap model).
 */
public class FloatMatrixModel implements Model, Rasterizable {
    private float[] data;
    private int width;
    private int height;
    private CoordsConverter converter;

    /**
     * Creates a new {@code FloatMatrixModel}.
     *
     * @param data      matrix data as one dimension float array of width*height elements
     * @param width     matrix width
     * @param height    matrix height
     * @param converter coordinates converter from matrix to world
     */
    public FloatMatrixModel(float[] data, int width, int height, CoordsConverter converter) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.converter = converter;
    }

    /**
     * Returns a rasterized chunk.
     *
     * @return rasterized chunk
     */
    @Override
    public IterableChunk2d getChunk() {
        try {
            return new FloatMatrixChunk(data, width, height, converter);
        } catch (TransformException e) {
            // TODO: Better log/warn/error here
            System.out.println("Unable to transform FloatMatrixModel to chunk!");
            return EmptyChunk2d.getInstance();
        }
    }
}
