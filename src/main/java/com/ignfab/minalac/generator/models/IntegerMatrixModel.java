package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.GeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * A model based on a matrix of integers.
 */
// TODO Refactor IntegerMatrixModel and FloatMatrixModel with a MatrixModel parent class
public class IntegerMatrixModel extends Model implements Matrix2d<Integer> {
    private final GeographicDataMatrix2d<Integer> data;
    private final MapToWorldConverter mapToWorld;
    private final WorldToMapConverter worldToMap;
    private final WorldBBox2d bbox;

    /**
     * Creates a new {@code IntegerMatrixModel}.
     *
     * @param data underlying geographic data
     * @param converter coordinates converter from matrix to world
     * @throws TransformException
     */
    public IntegerMatrixModel(GeographicDataMatrix2d<Integer> data, MapToWorldConverter converter) throws TransformException {
        mapToWorld = converter;
        try {
            worldToMap = mapToWorld.inverse();
        } catch (TransformException e) {
            throw new IllegalArgumentException("converter must be invertible");
        }

        this.data = data;

        double maxX = data.offsetX() + data.sizeX() * data.cellSizeX() - 1.0;
        double maxY = data.offsetY() + data.sizeY() * data.cellSizeY() - 1.0;

        this.bbox = new WorldBBox2d(
            mapToWorld.convert(new MapCoordinates(data.offsetX(), data.offsetY())),
            mapToWorld.convert(new MapCoordinates(data.offsetX(), maxY)),
            mapToWorld.convert(new MapCoordinates(maxX, data.offsetY())),
            mapToWorld.convert(new MapCoordinates(maxX, maxY))
        );
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public Integer get(WorldCoords2d coords) {
        // Here we perform coordinates conversion and value interpolation

        MapCoordinates coordinates;
        try {
            coordinates = worldToMap.convert(coords);
        } catch (TransformException e) {
            return null;
        }

        double x = (coordinates.x() - data.offsetX()) / data.cellSizeX();
        double y = (coordinates.y() - data.offsetY()) / data.cellSizeY();

        // TODO Int interpolation
        // Maybe separate regular int and color because color interpolation is different that regular int interpolation
        int xr = (int) Math.round(x);
        int yr = (int) Math.round(y);
        return data.get(xr, yr);
    }

    @Override
    public String salt() {
        // TODO: Seed mechanism for matrix cannot rely on feature (matrix model is a extract
        // of a unique huge data matrix).
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }
}
