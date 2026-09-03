package com.ignfab.minalac.generator.voxelization.matrix2d;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * An iterator over a Matrix2d that skips voxels without value.
 *
 * @param <T> type of value associated to voxels
 */
public class Matrix2dIterator<T> implements Iterator<Matrix2d.Value<T>> {
    private Matrix2d<T> matrix;
    private Iterator<WorldCoords2d> iterator;
    private Matrix2d.Value<T> current;

    /**
     * Creates a new iterator.
     *
     * @param matrix matrix on which iterate
     */
    public Matrix2dIterator(Matrix2d<T> matrix) {
        this.matrix = matrix;
        iterator = matrix.bbox().iterator();
        moveOn();
    }

    private void moveOn() {
        WorldCoords2d coords;
        T value;
        while (iterator.hasNext()) {
            coords = iterator.next();
            value = matrix.get(coords);

            if (value != null) {
                current = new Matrix2d.Value<T>(coords, value);
                return;
            }
        }
        current = null;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public Matrix2d.Value<T> next() {
        Matrix2d.Value<T> result = current;
        moveOn();
        return result;
    }
}
