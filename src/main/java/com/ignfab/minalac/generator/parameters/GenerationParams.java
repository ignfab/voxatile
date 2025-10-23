package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.JsonNode;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.tasks.TileTaskParams;

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
     * World name.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String worldName = "Minalac";

    /**
     * A placeholder for Yaml references than does not go anywhere else.
     * Content of this field will be ignored.
     */
    @JsonSetter(nulls = Nulls.SKIP)
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
    @JsonSetter(
        nulls = Nulls.SKIP,
        // To prevent null values on required field of an element of the map.
        contentNulls = Nulls.FAIL
    )
    public Map<String, HeightmapDeclarationParams> heightmaps = new LinkedHashMap<>();

    /**
     * Description of the schedule that will run for each tile.
     */
    @JsonSetter(
        nulls = Nulls.SKIP,
        contentNulls = Nulls.FAIL
    )
    public Map<String, TileTaskParams> forEachTile = new LinkedHashMap<>();

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param area the area of generation.
     * @param format the format of the game.
     */
    @ConstructorProperties({"area", "format"})
    public GenerationParams(Area area, OutputFormat format) {
        this.area = area;
        this.format = format;
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
        for (TileTaskParams params : forEachTile.values())
            params.validate();
    }

    /**
     * Creates the corresponding {@link Generation}.
     *
     * @param maxTileSize max tile size if tiling wanted, else null
     * @return the corresponding {@code Generation}
     */
    public Generation create(Integer maxTileSize) {
        return GenerationCreator.create(this, maxTileSize);
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
