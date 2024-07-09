package com.ignfab.minalac.generator.generation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.coordinates.WorldToMapConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Generation context class.
 * Contains stuff about ongoing generation and its context.
 */
public class Generation {
    // Target coordinate reference system (CRS used for voxel world rendering)
    private final CoordinateReferenceSystem crs;

    // Vertical size of voxel in target CRS units
    private final double verticalScale;

    // Transformations from and to target CRS
    private final AffineTransformation crsToVoxel;
    private final AffineTransformation voxelToCrs;

    // TODO: use a 3d bbox when its implemented:
    private final WorldBBox2d worldBBox;

    private final Map<String, Heightmap> heightmaps = new HashMap<>();

    /**
     * Constructs a new generation context.
     *
     * @param crs Coordinate reference system used for generated world
     * @param centerX Generation center x-coordinate (in generated world CRS)
     * @param centerY Generation center y-coordinate (in generated world CRS)
     * @param extendX Generated world size (in voxels) along x-coordinates
     * @param extendY Generated world size (in voxels) along y-coordinates
     * @param horizontalScale Horizontal size of voxel in CRS units
     * @param verticalScale Vertical size of voxel in CRS units
     */
    public Generation(
        CoordinateReferenceSystem crs,
        double centerX,
        double centerY,
        int extendX,
        int extendY,
        double horizontalScale,
        double verticalScale) {

        // For now:
        // - Center is in target CRS (should be lon/lat).
        // - Rotation is not yet implemented (should be).

        this.crs = crs;
        this.verticalScale = verticalScale;

        // Voxel BBox
        worldBBox = new WorldBBox2d(-extendX / 2, -extendY / 2, extendX, extendY);

        // CRS to Voxel transformation (basically, translates, rotates and scale)
        crsToVoxel = new AffineTransformation();
        crsToVoxel.translate(-centerX, -centerY);
        // crsToVoxel.rotate(rotate * pi / 180.0, 0.0, 0.0);
        crsToVoxel.scale(1.0 / horizontalScale, 1.0 / horizontalScale);

        // Voxel to CRS transformation (reverse of crsToVoxel transformation)
        voxelToCrs = new AffineTransformation();
        voxelToCrs.scale(horizontalScale, horizontalScale);
        // VoxelToCrs.rotate(- rotate * pi / 180.0, 0.0, 0.0);
        voxelToCrs.translate(centerX, centerY);
    }

    /**
     * Returns geographic envelope (bounding box equivalent) of generated world
     * in a given CRS.
     *
     * @param crs Coordinate reference system to get envelope for.
     * @return Envelope covering generated world in CRS.
     */
    public Envelope getEnvelopeForCRS(CoordinateReferenceSystem crs) throws FactoryException, TransformException {
        WorldToMapConverter converter = makeCoordsConverter(crs).inverse();

        Geometry geom = new GeometryFactory().createLinearRing(new Coordinate[] {
            new Coordinate(worldBBox.minX(), worldBBox.minY()),
            new Coordinate(worldBBox.maxX(), worldBBox.minY()),
            new Coordinate(worldBBox.maxX(), worldBBox.maxY()),
            new Coordinate(worldBBox.minX(), worldBBox.maxY()),
            new Coordinate(worldBBox.minX(), worldBBox.minY())
        });

        return converter.convert(geom).getEnvelopeInternal();
    }

    /**
     * Returns World 2d box in voxels.
     *
     * @return a WorldBBox2d representing horizontal world size
     */
    public WorldBBox2d getWorldBBox2d() {
        return worldBBox;
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
     * Registers a new {@link Heightmap} with the given name.
     * Name must be unique, case-sensitive.
     *
     * @param name      the name of the heightmap which will be used to identify it
     * @param heightmap the heightmap to be added
     * @throws IllegalArgumentException if the specified name is already registered or if the name is null
     */
    public void addHeightmap(String name, Heightmap heightmap) throws IllegalArgumentException {
        if (name == null || heightmaps.containsKey(name))
            throw new IllegalArgumentException("Illegal name for heightmap, duplicate or null name: " + name);
        heightmaps.put(name, heightmap);
    }

    /**
     * Returns the {@link Heightmap} associated to the given name.
     *
     * @param name the name of the heightmap
     * @return the associated heightmap
     * @throws NoSuchElementException if there is not a heightmap associated with the specified name
     */
    public Heightmap getHeightmap(String name) throws NoSuchElementException {
        Heightmap heightmap = heightmaps.get(name);
        if (heightmap == null)
            throw new NoSuchElementException("This heightmap does not exist: " + name);
        return heightmap;
    }
}
