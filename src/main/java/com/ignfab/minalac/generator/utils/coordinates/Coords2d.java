package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.geometry.Position2D;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;

/**
 * The {@code Coords2d} class represents two dimensional decimal (double float) coordinates.
 *
 * @param x The x-component value
 * @param y The y-component value
 */
public record Coords2d(double x, double y) {
    /**
     * Creates a {@code Coords2d} from {@code org.locationtech.jts.geom.Coordinate}.
     *
     * @param coordinate {@code Coordinate} to create {@code Coords2d} from
     */
    public Coords2d(Coordinate coordinate) {
        this(coordinate.x, coordinate.y);
    }

    private static Position check2d(Position position) {
        if (position.getDimension() != 2) throw new MismatchedDimensionException();
        return position;
    }
    /**
     * Creates a {@code Coords2d} from {@code org.geotools.geometry.Position}.
     *
     * @param pos {@code Position} to create {@code Coords2d} from
     */
    public Coords2d(Position pos) {
        this(check2d(pos).getOrdinate(0), pos.getOrdinate(1));
    }

    /**
     * Creates a {@code org.locationtech.jts.geom.Coordinate} from {@code Coords2d}.
     *
     * @return {@code Coordinate} created from {@code Coords2d}
     */
    public Coordinate toCoordinate() {
        return new CoordinateXY(this.x, this.y);
    }

    /**
     * Creates a {@code org.geotools.geometry.Position} from {@code Coords2d}.
     *
     * @return {@code Position} created from {@code Coords2d}
     */
    public Position toPosition() {
        return new Position2D(this.x, this.y);
    }
}
