package com.ignfab.minalac.generator.generation;

import java.util.Collection;
import java.util.Iterator;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.execution.Scheduler;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
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

    // Vertical size of voxel in target CRS units
    private final double verticalScale;

    // Transformations from and to target CRS
    private final AffineTransformation crsToVoxel;
    private final AffineTransformation voxelToCrs;

    private final VoxelWorld world;
    private final HeightmapDeclarationStore heightmaps = new HeightmapDeclarationStore();

    private final Scheduler forEachTileScheduler = new Scheduler();
    private final Scheduler afterAllTilesScheduler = new Scheduler();

    private final int maxTileSize;
    private final Collection<WorldBBox2d> tiles;

    private final Iterator<GenerationTile> tileIterator;

    private static Generation currentGeneration = null;

    /**
     * @return current generation.
     */
    public static Generation current() {
        if (currentGeneration == null)
            throw new IllegalStateException("No current generation!");
        return currentGeneration;
    }

    /* package private */ static void setCurrent(Generation generation) {
        currentGeneration = generation;
    }

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

        this.seed = seed;

        WorldBBox3d maximumLimits = world.maxLimits();
        world.setLimits(new WorldBBox3d(
            -extentX / 2,
            -extentY / 2,
            maximumLimits.minZ(),
            extentX,
            extentY,
            maximumLimits.sizeZ()
        ));

        this.world = world;

        this.maxTileSize = maxTileSize;
        tiles = world.tiles(maxTileSize);
        tileIterator = Iterators.remap(tiles.iterator(),
            bbox -> new GenerationTile(this, bbox.to3d(world.limits().minZ(), world.limits().sizeZ()))
        );

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

        // TODO: MANAGE THAT WITH SCOPED VALUES
        setCurrent(this);
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
     * {@return the "for each tile" generation scheduler}
     */
    public Scheduler forEachTileScheduler() {
        return forEachTileScheduler;
    }

    /**
     * {@return the "after all tiles" generation scheduler}
     */
    public Scheduler afterAllTilesScheduler() {
        return afterAllTilesScheduler;
    }

    /**
     * Returns geographic envelope (bounding box equivalent) of a given
     * bounding box in a given CRS.
     *
     * @param crs Coordinate reference system to get envelope for.
     * @param bbox Bounding box to get envelope for.
     * @return ReferencedEnvelope covering given bounding box in CRS.
     */
    public ReferencedEnvelope getEnvelopeForCRS(CoordinateReferenceSystem crs, WorldBBox3d bbox) throws FactoryException, TransformException {
        WorldToMapConverter converter = makeCoordsConverter(crs).inverse();

        // We include whole voxels surface (so +/- 0.5 around centers)
        Geometry geom = new GeometryFactory().createLinearRing(new Coordinate[] {
            new Coordinate(bbox.minX() - 0.5, bbox.minY() - 0.5),
            new Coordinate(bbox.maxX() + 0.5, bbox.minY() - 0.5),
            new Coordinate(bbox.maxX() + 0.5, bbox.maxY() + 0.5),
            new Coordinate(bbox.minX() - 0.5, bbox.maxY() + 0.5),
            new Coordinate(bbox.minX() - 0.5, bbox.minY() - 0.5)
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
     * {@return the vertical scale}
     */
    // To be removed when vertical is used by this class. (Renderers will probably contain that value)
    public double getVerticalScale() {
        return verticalScale;
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
     * Switches to next tile.
     *
     * @return false if no more tile
     */
    public boolean nextTile() {
        boolean hasNext = tileIterator.hasNext();
        GenerationTile.setCurrent(hasNext ? tileIterator.next() : null);
        return hasNext;
    }
}
