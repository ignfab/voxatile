package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates2d;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * A model based on a matrix of floats (usually a heightmap model).
 * The coordinates used to access its values are world coordinates.
 * Beware its values are not converted, it is up to the caller to make the necessary changes (For example convert altitude in world unit)
 */
public class FloatMatrixModel extends ModelImpl implements Matrix2d<Float> {
    private final FloatGeographicDataMatrix2d data;
    private final WorldToMapConverter worldToMap;
    private final WorldBBox2d bbox;

    /**
     * Creates a new {@code FloatMatrixModel}.
     *
     * @param data underlying geographic data
     * @param converter coordinates converter from matrix to world
     * @throws TransformException if converter is not invertible
     */
    public FloatMatrixModel(FloatGeographicDataMatrix2d data, MapToWorldConverter converter) throws TransformException {
        super(converter);
        try {
            worldToMap = converter.inverse();
        } catch (TransformException e) {
            throw new IllegalArgumentException("converter must be invertible");
        }

        this.data = data;

        double maxX = data.offsetX() + data.sizeX() * data.cellSizeX() - 1.0;
        double maxY = data.offsetY() + data.sizeY() * data.cellSizeY() - 1.0;

        this.bbox = new WorldBBox2d(
            converter.convert(new MapCoordinates2d(data.offsetX(), data.offsetY())),
            converter.convert(new MapCoordinates2d(data.offsetX(), maxY)),
            converter.convert(new MapCoordinates2d(maxX, data.offsetY())),
            converter.convert(new MapCoordinates2d(maxX, maxY))
        );
    }

    @Override
    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public Float get(WorldCoords2d coords) {
        // Here we perform coordinates conversion and value interpolation

        MapCoordinates2d coordinates;
        try {
            coordinates = worldToMap.convert(coords);
        } catch (TransformException e) {
            return null;
        }

        float x = (float) ((coordinates.x() - data.offsetX()) / data.cellSizeX());
        float y = (float) ((coordinates.y() - data.offsetY()) / data.cellSizeY());

        // Using separate ceil & floor allows a good management of integer coordinates
        int xf = (int) Math.floor(x);
        int yf = (int) Math.floor(y);
        int xc = (int) Math.ceil(x);
        int yc = (int) Math.ceil(y);
        if (xf < 0 || yf < 0 || xc >= data.sizeX() || yc >= data.sizeY())
            return null;

        // Basic bilinear interpolation
        float fx = x - xf;
        float fy = y - yf;

        return (1 - fy) * ((1 - fx) * data.getFloat(xf, yf) + fx * data.getFloat(xc, yf))
            + fy * ((1 - fx) * data.getFloat(xf, yc) + fx * data.getFloat(xc, yc));
    }

    @Override
    public String salt() {
        // TODO: Seed mechanism for matrix cannot rely on feature (matrix model is a extract
        // of a unique huge data matrix).
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }
}

