package com.ignfab.minalac.generator.utils.coordinates;

import org.locationtech.jts.geom.Geometry;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A world (game) to map (real) coordinates converter.
 * <p>
 * This wraps a {@link Converter} and provide method with adapted types.
 */
public class WorldToMapConverter {
    private final Converter converter;

    /**
     * Creates a new {@code WorldToMapConverter} out of a {@code CoordinatesConverter}.
     *
     * @param converter base converter to use
     */
    protected WorldToMapConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * Converts world (game) coordinates into map (real) coordinates.
     *
     * @param coords Coordinates in map
     * @return Corresponding coordinates in voxels, in decimal numbers
     * @throws TransformException if unable to perform transformation.
     */
    public MapCoordinates convert(WorldCoords2d coords) throws TransformException {
        return converter.convert(new MapCoordinates(coords.x(), coords.y()));
    }

    /**
     * Converts a JTS geometry.
     *
     * @param geom Geometry to transform.
     * @return Transformed geometry.
     * @throws TransformException if unable to perform transformation.
     */
    public Geometry convert(Geometry geom) throws TransformException {
        return converter.convert(geom);
    }
}
