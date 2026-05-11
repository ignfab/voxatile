package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.Position2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import tools.jackson.databind.JsonNode;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * GenerationParams represents the parameters used during the generation.
 * This class mirrors the structure of the object to be deserialized.
 * Required fields are initialized by the constructor.
 * The verification of their presence is done by the constructor via the {@code @ConstructorProperties} annotation, as it is currently the only supported method by the library.
 * @see <a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/625">GitHub issue about required fields during deserialization</a>.
 *
 * Refer to docs/usage/parameters/Parameters.md for parameters format.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationParams {
    /**
     * Dummy params.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public DummyParams dummy;

    /**
     * World name.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String worldName = "Minalac";

    /**
     * A placeholder for Yaml references than does not go anywhere else.
     * Content of this field will be ignored.
     */
    @JsonIgnore
    public JsonNode references;

    /**
     * Vertical scale (vertical size of voxel in meters).
     * This field is optional. (Default value: 1.0)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Double verticalScale = 1.0;

    /**
     * The horizontal scale (horizontal size of voxel in meters).
     * This field is optional. (Default value: 1.0)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Double horizontalScale = 1.0;

    /**
     * The area of generation.
     * This field is required during deserialization.
     */
    public Area area;

    /**
     * The CRS used when projecting in the world.
     * This field is optional.
     * Currently, default value when deserialized by {@code ParamsParser} is EPSG:2154.
     */
    public String crs;

    /**
     * The format of the generated map (required).
     * Format is one of formats registered using {@link ParamsParser#registerFormat(String, OutputFormat)}.
     */
    // Warning: If moved, path has to be updated in ParamsParser.parse().
    public OutputFormat format;

    /**
     * The random number seed (optional, default "").
     */
    public String seed;

    /**
     * Declarations of stored heightmaps used during the generation, by name (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP, contentNulls = Nulls.FAIL)
    public Map<String, HeightmapDeclarationParams> heightmaps = new LinkedHashMap<>();

    /**
     * Description of the schedule that will run for each tile.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ScheduleParams forEachTile = new ScheduleParams();

    /**
     * Description of the schedule that will run after all tiles.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public ScheduleParams afterAllTiles = new ScheduleParams();

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param area the area of generation.
     * @param format the format of the game.
     * @param dummy dummy params.
     */
    @ConstructorProperties({"area", "format", "dummy"})
    public GenerationParams(Area area, OutputFormat format, DummyParams dummy) {
        this.area = area;
        this.format = format;
        this.dummy = dummy;
        System.out.println(dummy);
    }

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {
        if (worldName.isBlank())
            throw new IllegalArgumentException("The field worldName cannot be empty or contain only whitespace.");
        if (verticalScale <= 0)
            throw new IllegalArgumentException("The field verticalScale must be greater than 0");
        if (horizontalScale <= 0)
            throw new IllegalArgumentException("The field horizontalScale must be greater than 0");
        if (-90 > area.center.latitude
            || area.center.latitude > 90
            || -180 > area.center.longitude
            || area.center.longitude > 180)
            throw new IllegalArgumentException("The coordinates of the center field are incorrect");
        if (area.extentX <= 0)
            throw new IllegalArgumentException("The field extentX must be greater than 0");
        if (area.extentY <= 0)
            throw new IllegalArgumentException("The field extentY must be greater than 0");

        for (HeightmapDeclarationParams params : heightmaps.values())
            params.validate();

        forEachTile.validate();
        afterAllTiles.validate();
    }

    /**
     * Creates the corresponding {@link Generation}.
     *
     * @param destination where to write generated world
     * @param maxTileSize max tile size if tiling wanted, else null
     * @return the corresponding {@code Generation}
     */
    public Generation create(File destination, Integer maxTileSize) {
        // TODO : If not provided its default value should be calculated by finding the appropriated projected CRS for the provided center point.
        // At the moment the default value is EPSG:2154
        CoordinateReferenceSystem targetCrs;
        double[] center;
        try {
            targetCrs = CRS.decode(crs == null ? "EPSG:2154" : crs);
            MathTransform mathTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, targetCrs);
            Position position = new Position2D(DefaultGeographicCRS.WGS84, area.center.longitude, area.center.latitude);
            center = mathTransform.transform(position, position).getCoordinate();
        } catch (FactoryException e) {
            throw new RuntimeException("Wasn't able to decode the CRS code", e);
        } catch (TransformException e) {
            throw new RuntimeException("Wasn't able to convert the coordinates", e);
        }

        VoxelWorld world = format.createWorld(destination);
        world.getMetadata().setWorldName(worldName);
        Generation generation = new Generation(
            world,
            new Seed(seed),
            targetCrs,
            center[0],
            center[1],
            area.extentX,
            area.extentY,
            horizontalScale,
            verticalScale,
            Math.toRadians(area.angle),
            maxTileSize == null || maxTileSize <= 0 ? Math.max(area.extentX, area.extentY) : maxTileSize
        );

        heightmaps.forEach((name, heightmapParams) ->
            generation.heightmaps().add(heightmapParams.create(name))
        );

        // ForEachTile scheduling
        forEachTile.populate(generation, generation.forEachTileScheduler());
        // AfterAllTiles scheduling
        afterAllTiles.populate(generation, generation.afterAllTilesScheduler());

        return generation;
    }

    /**
     * Area to be rendered.
     */
    public static class Area {
        /**
         * Longitude and latitude coordinates.
         */
        public static class LatitudeLongitude {
            /**
             * Latitude.
             * This field is required.
             */
            public double latitude;
            /**
             * Longitude.
             * This field is required.
             */
            public double longitude;

            /**
             * Constructor used to ensure that the required fields are present during deserialization.
             *
             * @param latitude  the latitude of the center.
             * @param longitude the longitude of the center.
             */
            @ConstructorProperties({"latitude", "longitude"})
            LatitudeLongitude(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }

        /**
         * The center of the area.
         * This field is required during deserialization.
         */
        public LatitudeLongitude center;
        /**
         * Extent in voxel along the x-axis.
         * This field is required during deserialization.
         */
        public int extentX;
        /**
         * Extent in voxel along the y-axis.
         * This field is required during deserialization.
         */
        public int extentY;

        /**
         * Rotation angle around center in degrees.
         * Default 0.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public double angle = 0d;

        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         *
         * @param center  the center of the area.
         * @param extentX the extent in voxel along the x-axis.
         * @param extentY the extent in voxel along the y-axis.
         */
        @ConstructorProperties({ "center", "extentX", "extentY" })
        Area(LatitudeLongitude center, int extentX, int extentY) {
            this.center = center;
            this.extentX = extentX;
            this.extentY = extentY;
        }
    }
}
