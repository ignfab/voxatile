package com.ignfab.minalac.generator.models;

import java.util.Iterator;

import org.geotools.api.referencing.operation.TransformException;

import com.ignfab.minalac.generator.generation.CoordsConverter;
import com.ignfab.minalac.generator.utils.coordinates.Coords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIteratorSkip;

/**
 * A chunk embedding a float matrix.
 * The float matrix is kept "as is", values corresponding to integer coordinates (after projection and rotation) are computed on the fly.
 */
public class FloatMatrixChunk implements IterableChunk2d {
    /**
     * Value returned if not able to give any data.
     */
    public static final int OUTSIDE = Integer.MIN_VALUE;

    private float[] data;
    private int sizeX;
    private int sizeY;
    private CoordsConverter converter;
    private WorldBBox2d bbox;

    /**
     * Creates a new FloatMatrixChunk.
     *
     * @param data Data as float array, must be of sizeX*sizeY elements
     * @param sizeX Size of matrix on x-axis
     * @param sizeY Size of matrix on y-axis
     * @param converter Converter to use to convert from matrix coordinates to world coordinates (must be invertible)
     */
    public FloatMatrixChunk(float[] data, int sizeX, int sizeY, CoordsConverter converter) throws TransformException {
        if (data.length != sizeX * sizeY)
            throw new IllegalArgumentException("data length must correspond to given sizes product");

        if (!converter.isInvertible())
            throw new IllegalArgumentException("converter must be invertible");

        this.data = data;
        this.converter = converter;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        // Here, we compute world bbox englobing the transformed float matrix chunk
        // +/- 0.5 to ensure we really have voxels on edge (or we may an entire line of non computable voxels)
        this.bbox = new WorldBBox2d(
            converter.convert(new Coords2d(0.5, 0.5)),
            converter.convert(new Coords2d(0.5, sizeY - 0.5)),
            converter.convert(new Coords2d(sizeX - 0.5, 0.5)),
            converter.convert(new Coords2d(sizeX - 0.5, sizeY - 0.5))
        );
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public int get(int x, int y) {
        return get(new WorldCoords2d(x, y));
    }

    private int index(int x, int y) {
        return  x + (sizeY - y - 1) * sizeX;
    }
    @Override
    public int get(WorldCoords2d coords) {
        Coords2d coordinates;
        try {
            coordinates = converter.reverse(coords);
        } catch (TransformException e) {
            return OUTSIDE;
        }

        // Using separate ceil & floor allows a good management of integer coordinates
        int xf = (int) Math.floor(coordinates.x());
        int yf = (int) Math.floor(coordinates.y());
        int xc = (int) Math.ceil(coordinates.x());
        int yc = (int) Math.ceil(coordinates.y());
        if (xf < 0 || yf < 0 || xc >= sizeX || yc >= sizeY)
            return OUTSIDE;

        // Basic bilinear interpolation
        double fx = coordinates.x() - xf;
        double fy = coordinates.y() - yf;

        return (int) Math.round(
            (1 - fy) * ((1 - fx) * data[index(xf, yf)] + fx * data[index(xc, yf)])
            + fy * ((1 - fx) * data[index(xf, yc)] + fx * data[index(xc, yc)])
        );
    }

    @Override
    public Iterator<Chunk2dElement> iterator() {
        return new Chunk2dIteratorSkip(this, OUTSIDE);
    }
}
