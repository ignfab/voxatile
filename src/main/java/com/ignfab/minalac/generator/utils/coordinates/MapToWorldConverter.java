package com.ignfab.minalac.generator.utils.coordinates;

import java.awt.geom.AffineTransform;
import javax.measure.Unit;

import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.MathTransformFactory;
import org.geotools.api.referencing.operation.NoninvertibleTransformException;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.geotools.referencing.util.CRSUtilities;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;


/**
 * Converter capable of converting coordinates/distances from a map CRS (real) to world (game) coordinates/distances.
 */
public class MapToWorldConverter {
    private final MathTransform transform;
    private final double horizontalScale; // (voxel / map unit)
    // There might be an additional vertical scale to mitigate the fact that Minecraft is limited in vertical length.
    private final double verticalScale; // (voxel / map unit)
    // TODO: See also comment on Generation makeCoordsConverter()
    // Unused for now, this value is meant to store the possible offset between different CRS
    // and possibly a non-geographical offset to mitigate the fact that Minecraft is limited in vertical length.
    private final double altitudeOffset;

    /**
     * Creates a new {@code MapToWorldConverter} from the given parameters.
     * It allows conversion of coordinates expressed in map CRS (real) into world coordinates (game).
     * Roughly it applies two transformations
     * <ul>
     *     <li>Map CRS to world CRS</li>
     *     <li>Post affine transformation: translation, then horizontal scaling, then rotation</li>
     * </ul>
     * Since post affine transformation can be applied, world CRS must be a cartesian coordinate system otherwise result might be awry.
     *
     * @param mapCrs Coordinate reference system used by the map
     * @param worldCrs Coordinate reference system used for generated world, it must a Cartesian coordinate system
     * @param translateX x-translation to apply in voxel unit
     * @param translateY y-translation to apply in voxel unit
     * @param horizontalScale Horizontal size of voxel in world CRS units (voxel / world unit)
     * @param verticalScale Vertical size of voxel in world CRS units (voxel / world unit)
     * @param angle Rotation angle around center in radians
     * @param altitudeOffset the altitude offset to apply in voxel units
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public MapToWorldConverter(
        CoordinateReferenceSystem mapCrs,
        CoordinateReferenceSystem worldCrs,
        double translateX,
        double translateY,
        double horizontalScale,
        double verticalScale,
        double angle,
        double altitudeOffset
    ) {
        // Checking units once
        Unit<?> unitMap = CRSUtilities.getUnit(mapCrs.getCoordinateSystem());
        Unit<?> unitWorld = CRSUtilities.getUnit(worldCrs.getCoordinateSystem());
        if (!unitMap.equals(unitWorld))
            System.out.printf("WARNING: the two CRS do not use the same unit. They might be awry results when converting distances. Unit map: %s, Unit world: %s%n", unitMap.getName(), unitWorld.getName());

        MathTransform transform;
        try {
            transform = chain(
                CRS.findMathTransform(mapCrs, worldCrs),
                new AffineTransform2D(AffineTransform.getTranslateInstance(translateX, translateY)),
                new AffineTransform2D(AffineTransform.getScaleInstance(horizontalScale, horizontalScale)),
                new AffineTransform2D(AffineTransform.getRotateInstance(angle))
            );
        } catch (FactoryException e) {
            // FactoryException will most likely occur if the CRS transform can not be created
            throw new IllegalArgumentException("Could not create map transformation from CRS parameters", e);
        }

        this.transform = transform;
        // The scales might be passed to the transform object, but it lost during creation
        // The same value should be passed explicitly as it is required for convertAltitude(), convertHorizontalDistance(), convertVerticalDistance()
        this.horizontalScale = horizontalScale;
        this.verticalScale = verticalScale;
        this.altitudeOffset = altitudeOffset;
    }

    /**
     * {@return a new {@link WorldToMapConverter} capable of performing the inverse coordinates conversion}
     * @throws TransformException if it is not possible to create the inverse conversion.
     */
    public WorldToMapConverter inverse() throws TransformException {
        if (verticalScale == 0.0 || horizontalScale == 0.0)
            throw new TransformException("Converter is not invertible because of scales");
        try {
            return new WorldToMapConverter(transform.inverse());
        } catch (NoninvertibleTransformException e) {
            throw new TransformException("Converter is not invertible because of map transformation", e);
        }
    }

    /**
     * Convert {@code MapCoordinates2d} coordinates.
     * The result is rounded down.
     *
     * @param coords the coordinates expressed in map CRS.
     * @return the converted coordinates as a {@code WorldCoords2d}.
     * @throws TransformException if conversion can not be performed
     */
    public WorldCoords2d convert(MapCoordinates2d coords) throws TransformException {
        Position position = convert(coords.x(), coords.y());
        return WorldCoords2d.floor(position.getOrdinate(0), position.getOrdinate(1));
    }

    /**
     * Convert {@code MapCoordinates3d} coordinates.
     * The result is rounded down.
     *
     * @param coords the coordinates expressed in map CRS.
     * @return the converted coordinates as a {@code WorldCoords3d}.
     * @throws TransformException if conversion can not be performed
     */
    public WorldCoords3d convert(MapCoordinates3d coords) throws TransformException {
        Position position = convert(coords.x(), coords.y());
        return WorldCoords3d.floor(position.getOrdinate(0), position.getOrdinate(1), convertAltitude(coords.z()));
    }

    private Position convert(double x, double y) throws TransformException {
        try {
            return transform.transform(new Position2D(x, y), null);
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform coordinates",  e);
        }
    }

    /**
     * Converts a JTS geometry.
     *
     * @param geom the geometry to transform
     * @return the transformed geometry
     * @throws TransformException if transformation can not be performed
     */
    public Geometry convert(Geometry geom) throws TransformException {
        try {
            Geometry g = JTS.transform(geom, transform);
            g.apply(new AltitudeFilter());
            return g;
        } catch (org.geotools.api.referencing.operation.TransformException e) {
            throw new TransformException("Could not transform geometry", e);
        }
    }

    /**
     * Converts the altitude.
     * No unit conversion is performed, it assumes that map CRS and world CRS use the same unit.
     *
     * @param altitude expressed in map CRS unit.
     * @return the converted altitude expressed in voxel unit.
     */
    public int convertAltitude(double altitude) {
        return (int) Math.floor((altitude + altitudeOffset) * verticalScale);
    }

    /**
     * Converts the given horizontal distance.
     * No unit conversion is performed, it assumes that map CRS and world CRS use the same unit.
     *
     * @param distance expressed in map CRS unit.
     * @return the converted distance expressed in voxel unit.
     */
    public int convertHorizontalDistance(double distance) {
        return (int) Math.floor(distance * horizontalScale);
    }

    /**
     * Converts a given vertical distance.
     * No unit conversion is performed, it assumes that map CRS and world CRS use the same unit.
     *
     * @param distance expressed in map CRS unit.
     * @return the converted distance expressed in voxel unit.
     */
    public int convertVerticalDistance(double distance) {
        return (int) Math.floor(distance * verticalScale);
    }

    private static MathTransform chain(MathTransform first, MathTransform... others) throws FactoryException {
        MathTransformFactory factory = new DefaultMathTransformFactory();
        MathTransform result = first;
        for (MathTransform other : others)
            result = factory.createConcatenatedTransform(result, other);
        return result;
    }

    private final class AltitudeFilter implements CoordinateFilter {

        @Override
        public void filter(Coordinate coord) {
            coord.z = convertAltitude(coord.z);
        }
    }

}
