package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.Position2D;
import org.locationtech.jts.geom.Coordinate;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Converter capable of converting coordinates expressed in voxels coordinates (game) to map coordinates (real).
 * It should be constructed using {@link MapToWorldConverter#inverse()}.
 */
public class WorldToMapConverter {
    private final MathTransform transform;

    /**
     * Constructs a new {@code WorldToMapConverter} by passing a {@code MathTransform}.
     *
     * @param transform the transformation object to use.
     */
    protected WorldToMapConverter(MathTransform transform) {
        this.transform = transform;
    }

    /**
     * Convert {@code WorldCoords2d} coordinates.
     *
     * @param coords the world (game) coordinates.
     * @return the converted coordinates as a {@code MapCoordinates2d}.
     * @throws TransformException if conversion can not be performed
     */
    public MapCoordinates2d convert(WorldCoords2d coords) throws TransformException {
        Position position = convert(coords.x(), coords.y());
        return new MapCoordinates2d(position.getOrdinate(0), position.getOrdinate(1));

    }

    /**
     * Convert JTS coordinates.
     *
     * @param coords the world (game) coordinates as a {@code Coordinate} (JTS).
     * @return the converted coordinates as a JTS coordinates.
     * @throws TransformException if conversion can not be performed
     */
    public Coordinate convert(Coordinate coords) throws TransformException {
        Position position = convert(coords.x, coords.y);
        return new Coordinate(position.getOrdinate(0), position.getOrdinate(1));
    }

    private Position convert(double x, double y) throws TransformException {
        try {
            return transform.transform(new Position2D(x, y), null);
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform coordinates",  e);
        }
    }
}
