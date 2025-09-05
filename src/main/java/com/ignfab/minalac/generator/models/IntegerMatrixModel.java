package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.IntegerGeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * A model based on a matrix of floats (usually a heightmap model).
 */
public class IntegerMatrixModel extends MatrixModel implements Matrix2d<Integer> {
    private IntegerGeographicDataMatrix2d data;

    /**
     * Creates a new {@code FloatMatrixModel}.
     *
     * @param data underlying geographic data
     * @param converter coordinates converter from matrix to world
     * @throws TransformException
     */
    public IntegerMatrixModel(IntegerGeographicDataMatrix2d data, MapToWorldConverter converter) throws TransformException {
        super(data, converter);
        this.data = data;
    }

    @Override
    public Integer get(WorldCoords2d coords) {
        // Here we perform coordinates conversion
        // Unlike float matrix, no interpolation is made here, we take nearest point

        MapCoordinates coordinates;
        try {
            coordinates = worldToMap.convert(coords);
        } catch (TransformException e) {
            return null;
        }

        return data.getInt(
            (int) Math.round((coordinates.x() - data.offsetX()) / data.cellSizeX()),
            (int) Math.round((coordinates.y() - data.offsetY()) / data.cellSizeY())
        );
    }
}

