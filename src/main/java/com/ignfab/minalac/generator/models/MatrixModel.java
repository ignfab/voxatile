package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.GeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class MatrixModel extends ModelImpl {
    protected MapToWorldConverter mapToWorld;
    protected WorldToMapConverter worldToMap;
    private WorldBBox2d bbox;

    public MatrixModel(GeographicDataMatrix2d<?> matrix, MapToWorldConverter converter) throws TransformException {
        mapToWorld = converter;
        try {
            worldToMap = mapToWorld.inverse();
        } catch (TransformException e) {
            throw new IllegalArgumentException("converter must be invertible");
        }

        double maxX = matrix.offsetX() + matrix.sizeX() * matrix.cellSizeX() - 1.0;
        double maxY = matrix.offsetY() + matrix.sizeY() * matrix.cellSizeY() - 1.0;

        this.bbox = new WorldBBox2d(
            mapToWorld.convert(new MapCoordinates(matrix.offsetX(), matrix.offsetY())),
            mapToWorld.convert(new MapCoordinates(matrix.offsetX(), maxY)),
            mapToWorld.convert(new MapCoordinates(maxX, matrix.offsetY())),
            mapToWorld.convert(new MapCoordinates(maxX, maxY))
        );
    }

    public WorldBBox2d bbox() {
        return bbox;
    }

    @Override
    public String salt() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }

}
