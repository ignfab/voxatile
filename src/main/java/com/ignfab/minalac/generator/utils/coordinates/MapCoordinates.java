package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.locationtech.jts.geom.Coordinate;

/**
 * The {@code MapCoordinates} class represents two dimensional decimal (double float) coordinates.
 *
 * @param x The x-component value
 * @param y The y-component value
 */
public record MapCoordinates(double x, double y) {
    /**
     * Creates a {@code MapCoordinates} from {@code org.locationtech.jts.geom.Coordinate}.
     *
     * @param coordinate {@code Coordinate} to create {@code MapCoordinates} from
     */
    public MapCoordinates(Coordinate coordinate) {
        this(coordinate.x, coordinate.y);
    }

    private static Position check2d(Position position) {
        if (position.getDimension() != 2) throw new MismatchedDimensionException();
        return position;
    }
    /**
     * Creates a {@code MapCoordinates} from {@code org.geotools.geometry.Position}.
     *
     * @param pos {@code Position} to create {@code MapCoordinates} from
     */
    public MapCoordinates(Position pos) {
        this(check2d(pos).getOrdinate(0), pos.getOrdinate(1));
    }
}
