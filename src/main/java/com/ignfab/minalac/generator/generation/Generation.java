package com.ignfab.minalac.generator.generation;

import java.util.Collection;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * This {@code Generation} class contains information about the ongoing generation
 * such as the voxel world, the scheduler or the heightmaps.
 */
public class Generation {
    // Main generation seed for random number generation
    private final Seed seed;

    // Target coordinate reference system (CRS used for voxel world rendering)
    private final CoordinateReferenceSystem crs;

    private final double centerX;
    private final double centerY;
    // Horizontal size of voxel in target CRS units (map unit / voxel)
    private final double horizontalScale;
    // Vertical size of voxel in target CRS units (map unit / voxel)
    private final double verticalScale;
    private final double angle;

    private final VoxelWorld world;
    private final HeightmapDeclarationStore heightmaps = new HeightmapDeclarationStore();

    private final Scheduler<GenerationTile> scheduler = new Scheduler<>();

    private final int maxTileSize;
    private final Collection<WorldBBox2d> tiles;

    /**
     * Constructs a new generation context.
     * It sets {@code VoxelWorld}'s limits in way the center is at {@code WorldCoords2d} (0, 0).
     *
     * @param world the voxel world without its {@code limits()} set
     * @param seed Seed for random number generation
     * @param crs Coordinate reference system used for generated world
     * @param centerX first coordinate of the center in the specified CRS
     * @param centerY second coordinate of the center in the specified CRS
     * @param extentX Generated world size (in voxels) along x-coordinates
     * @param extentY Generated world size (in voxels) along y-coordinates
     * @param horizontalScale Horizontal size of voxel in CRS units
     * @param verticalScale Vertical size of voxel in CRS units
     * @param angle Rotation angle around center in radians
     * @param maxTileSize Maximum tile size
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Generation(
        VoxelWorld world,
        Seed seed,
        CoordinateReferenceSystem crs,
        double centerX,
        double centerY,
        int extentX,
        int extentY,
        double horizontalScale,
        double verticalScale,
        double angle,
        int maxTileSize) {

        WorldBBox3d maximumLimits = world.maxLimits();
        world.setLimits(new WorldBBox3d(
            -extentX / 2,
            -extentY / 2,
            maximumLimits.minZ(),
            extentX,
            extentY,
            maximumLimits.sizeZ()
        ));

        this.seed = seed;
        this.crs = crs;
        this.centerX = centerX;
        this.centerY = centerY;
        this.horizontalScale = horizontalScale;
        this.verticalScale = verticalScale;
        this.angle = angle;
        this.world = world;
        this.maxTileSize = maxTileSize;
        tiles = world.tiles(maxTileSize);
    }

    /**
     * Returns the {@code VoxelWorld} with the limits corresponding the specified parameters for this {@code Generation}.
     * @return the voxel world
     */
    public VoxelWorld world() {
        return world;
    }

    /**
     * Returns the {@link HeightmapDeclarationStore} for the stored heightmaps.
     *
     * @return the heightmap declaration store.
     */
    public HeightmapDeclarationStore heightmaps() {
        return heightmaps;
    }

    /**
     * {@return the generation scheduler}
     */
    public Scheduler<GenerationTile> scheduler() {
        return scheduler;
    }

    /**
     * Returns geographic envelope (bounding box equivalent) of a given
     * bounding box in a given CRS.
     *
     * @param crs Coordinate reference system to get envelope for.
     * @param bbox Bounding box to get envelope for.
     * @return ReferencedEnvelope covering given bounding box in CRS.
     */
    public ReferencedEnvelope getEnvelopeForCRS(CoordinateReferenceSystem crs, WorldBBox3d bbox) throws TransformException {
        WorldToMapConverter converter = makeCoordsConverter(crs).inverse();

        int minX = bbox.minX();
        int minY = bbox.minY();
        int maxX = bbox.maxX() + 1;
        int maxY = bbox.maxY() + 1;
        Geometry geom = new GeometryFactory().createLinearRing(new Coordinate[] {
            converter.convert(new Coordinate(minX, minY)),
            converter.convert(new Coordinate(maxX, minY)),
            converter.convert(new Coordinate(maxX, maxY)),
            converter.convert(new Coordinate(minX, maxY)),
            converter.convert(new Coordinate(minX, minY))
        });

        return new ReferencedEnvelope(geom.getEnvelopeInternal(), crs);
    }

    /**
     * Creates a converter for a given CRS.
     *
     * @param sourceCrs CRS from which convert map coordinates.
     * @return A converter to be used to convert any map coordinates into generated world coordinates.
     */
    public MapToWorldConverter makeCoordsConverter(CoordinateReferenceSystem sourceCrs) {
        // altitudeOffset will be possibly be used to store offset between different CRS.
        // If that is the case, the world CRS should be a CRS containing an altitude of reference .
        return new MapToWorldConverter(sourceCrs, crs, -centerX, -centerY, 1.0 / horizontalScale, 1.0 / verticalScale, -angle, -0.0);
    }

    /**
     * {@return random number seed for this generation}
     */
    public Seed seed() {
        return seed;
    }

    /**
     * {@return target CRS, used for world rendering}
     */
    public CoordinateReferenceSystem crs() {
        return crs;
    }

    /**
     * {@return maximum generation tile size}
     */
    public int maxTileSize() {
        return this.maxTileSize;
    }

    /**
     * {@return number of generation tiles}
     */
    public int numberOfTiles() {
        return this.tiles.size();
    }

    /**
     * {@return an iterable over generation tiles}
     */
    public Iterable<GenerationTile> tiles() {
        return Iterables.remap(tiles,
            bbox -> new GenerationTile(this, bbox.to3d(world.limits().minZ(), world.limits().sizeZ()))
        );
    }
}
