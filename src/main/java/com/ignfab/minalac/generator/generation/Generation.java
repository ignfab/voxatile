package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelWorld;
import com.ignfab.minalac.generator.utils.random.Seed;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

/**
 * This {@code Generation} class contains information about the ongoing generation
 * such as the voxel world, the scheduler or the heightmaps.
 */
public class Generation {
    // Main generation seed for random number generation
    private final Seed seed;

    // Target coordinate reference system (CRS used for voxel world rendering)
    private final CoordinateReferenceSystem crs;

    // Vertical size of voxel in target CRS units
    private final double verticalScale;

    // Transformations from and to target CRS
    private final AffineTransformation crsToVoxel;
    private final AffineTransformation voxelToCrs;

    private final VoxelWorld world;
    private final ModelStore models = new ModelStore();
    private final Store<Heightmap> heightmaps = new Store<>();

    private final Scheduler scheduler = new Scheduler();

    /**
     * Constructs a new generation context.
     * It sets {@code VoxelWorld}'s limits in way the center is at {@code WorldCoords2d} (0, 0).
     *
     * @param world the voxel world without its {@code limits()} set
     * @param seed Seed for random number generation
     * @param crs Coordinate reference system used for generated world
     * @param centerX first coordinate of the center in the specified CRS
     * @param centerY second coordinate of the center in the specified CRS
     * @param extendX Generated world size (in voxels) along x-coordinates
     * @param extendY Generated world size (in voxels) along y-coordinates
     * @param horizontalScale Horizontal size of voxel in CRS units
     * @param verticalScale Vertical size of voxel in CRS units
     * @param angle Rotation angle around center in radians
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Generation(
        VoxelWorld world,
        Seed seed,
        CoordinateReferenceSystem crs,
        double centerX,
        double centerY,
        int extendX,
        int extendY,
        double horizontalScale,
        double verticalScale,
        double angle) {

        this.seed = seed;

        WorldBBox3d maximumLimits = world.maxLimits();
        world.setLimits(new WorldBBox3d(
            -extendX / 2,
            -extendY / 2,
            maximumLimits.minZ(),
            extendX,
            extendY,
            maximumLimits.sizeZ()
        ));

        this.world = world;
        this.crs = crs;
        this.verticalScale = verticalScale;

        // CRS to Voxel transformation (basically, translates, rotates and scale)
        crsToVoxel = new AffineTransformation();
        crsToVoxel.translate(-centerX, -centerY);
        crsToVoxel.scale(1.0 / horizontalScale, 1.0 / horizontalScale);
        crsToVoxel.rotate(-angle);

        // Voxel to CRS transformation (reverse of crsToVoxel transformation)
        voxelToCrs = new AffineTransformation();
        voxelToCrs.rotate(angle);
        voxelToCrs.scale(horizontalScale, horizontalScale);
        voxelToCrs.translate(centerX, centerY);
    }

    /**
     * Returns the {@code VoxelWorld} with the limits corresponding the specified parameters for this {@code Generation}.
     * @return the voxel world
     */
    public VoxelWorld world() {
        return world;
    }

    /**
     * Returns the {@link ModelStore}.
     * @return the model store
     */
    public ModelStore models() {
        return models;
    }

    /**
     * Returns the {@link Store} for the heightmaps.
     * @return the heightmaps.
     */
    public Store<Heightmap> heightmaps() {
        return heightmaps;
    }

    /**
     * Returns the generation scheduler.
     * @return the scheduler.
     */
    public Scheduler scheduler() {
        return scheduler;
    }

    /**
     * Returns geographic envelope (bounding box equivalent) of generated world
     * in a given CRS.
     *
     * @param crs Coordinate reference system to get envelope for.
     * @return ReferencedEnvelope covering generated world in CRS.
     */
    public ReferencedEnvelope getEnvelopeForCRS(CoordinateReferenceSystem crs) throws FactoryException, TransformException {
        WorldToMapConverter converter = makeCoordsConverter(crs).inverse();

        int minX = world.limits().minX();
        int minY = world.limits().minY();
        int maxX = world.limits().maxX() + 1;
        int maxY = world.limits().maxY() + 1;
        Geometry geom = new GeometryFactory().createLinearRing(new Coordinate[] {
            new Coordinate(minX, minY),
            new Coordinate(maxX, minY),
            new Coordinate(maxX, maxY),
            new Coordinate(minX, maxY),
            new Coordinate(minX, minY)
        });

        return new ReferencedEnvelope(converter.convert(geom).getEnvelopeInternal(), crs);
    }

    /**
     * Creates a converter for a given CRS.
     *
     * @param sourceCrs CRS from which convert map coordinates.
     * @return A converter to be used to convert any map coordinates into generated world coordinates.
     * @throws FactoryException If not suitable transformation found for conversion.
     */
    public MapToWorldConverter makeCoordsConverter(CoordinateReferenceSystem sourceCrs) throws FactoryException {
        return new MapToWorldConverter(CRS.findMathTransform(sourceCrs, crs), crsToVoxel);
    }

    /**
     * Returns the vertical scale.
     *
     * @return the vertical scale
     */
    // To be removed when vertical is used by this class. (Renderers will probably contain that value)
    public double getVerticalScale() {
        return verticalScale;
    }

    /**
     * Returns the random number seed for this generation.
     *
     * @return the seed
     */
    public Seed seed() {
        return seed;
    }

    /**
     * Returns target CRS.
     *
     * @return CRS used for world rendering
     */
    public CoordinateReferenceSystem crs() {
        return crs;
    }

}
