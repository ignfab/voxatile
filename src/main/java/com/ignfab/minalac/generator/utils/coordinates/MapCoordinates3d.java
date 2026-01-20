package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.locationtech.jts.geom.Coordinate;

/**
 * The {@code MapCoordinates} class represents three dimensional decimal (double float) coordinates.
 *
 * @param x The x-component value
 * @param y The y-component value
 * @param z The z-component value
 */
public record MapCoordinates3d(double x, double y, double z) {
    /**
     * Creates a {@code MapCoordinates} from {@code org.locationtech.jts.geom.Coordinate}.
     *
     * @param coordinate {@code Coordinate} to create {@code MapCoordinates} from
     */
    public MapCoordinates3d(Coordinate coordinate) {
        this(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    }

    private static Position check3d(Position position) {
        if (position.getDimension() != 3) throw new MismatchedDimensionException();
        return position;
    }
    /**
     * Creates a {@code MapCoordinates} from {@code org.geotools.geometry.Position}.
     *
     * @param pos {@code Position} to create {@code MapCoordinates} from
     */
    public MapCoordinates3d(Position pos) {
        this(check3d(pos).getOrdinate(0), pos.getOrdinate(1), pos.getOrdinate(2));
    }
}
