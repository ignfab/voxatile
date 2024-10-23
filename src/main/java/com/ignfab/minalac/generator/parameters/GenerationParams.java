package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.renderers.RendererParams;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GenerationParams represents the parameters used during the generation.
 * This class mirrors the structure of the object to be deserialized.
 * Required fields are initialized by the constructor.
 * The verification of their presence is done by the constructor via the {@code @ConstructorProperties} annotation, as it is currently the only supported method by the library.
 * @see <a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/625">GitHub issue about required fields during deserialization</a>.
 *
 * Refer to docs/usage/GenerationParameters.md for parameters format.
 */
// Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationParams {
    // For now :
    // - field mapName is not yet implemented (should probably be)

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
     * Heightmaps used during the generation, by name (optional).
     */
    @JsonSetter(
        nulls = Nulls.SKIP,
        // To prevent null values on required field of an element of the map.
        contentNulls = Nulls.FAIL
    )
    public Map<String, HeightmapParams> heightmaps = new LinkedHashMap<>();

    /**
     * Sources used during the generation, by name (optional).
     */
    @JsonSetter(
        nulls = Nulls.SKIP,
        // To prevent null values on required field of an element of the map.
        contentNulls = Nulls.FAIL
    )
    public Map<String, DataSourceParams> sources = new LinkedHashMap<>();

    /**
     * Renderers used during the generation, by name (optional).
     */
    @JsonSetter(
        nulls = Nulls.SKIP,
        // To prevent null values on required field of an element of the map.
        contentNulls = Nulls.FAIL
    )
    Map<String, RendererParams> renderers = new LinkedHashMap<>();

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
        if (verticalScale <= 0)
            throw new IllegalArgumentException("The field verticalScale must be greater than 0");
        if (horizontalScale <= 0)
            throw new IllegalArgumentException("The field horizontalScale must be greater than 0");
        if (-90 > area.center.latitude
            || area.center.latitude > 90
            || -180 > area.center.longitude
            || area.center.longitude > 180)
            throw new IllegalArgumentException("The coordinates of the center field are incorrect");
        if (area.extendX <= 0)
            throw new IllegalArgumentException("The field extendX must be greater than 0");
        if (area.extendY <= 0)
            throw new IllegalArgumentException("The field extendY must be greater than 0");

        for (HeightmapParams params : heightmaps.values())
            params.validate();
        for (DataSourceParams params : sources.values())
            params.validate();
        for (RendererParams params : renderers.values())
            params.validate();
    }

    /**
     * Creates the corresponding {@link Generation}.
     *
     * @return the corresponding {@code Generation}
     */
    public Generation create() {
        return GenerationCreator.create(this);
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
         * Extends in voxel along the x-axis.
         * This field is required during deserialization.
         */
        public int extendX;
        /**
         * Extends in voxel along the y-axis.
         * This field is required during deserialization.
         */
        public int extendY;

        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         *
         * @param center  the center of the area.
         * @param extendX the extends in voxel along the x-axis.
         * @param extendY the extends in voxel along the y-axis.
         */
        @ConstructorProperties({ "center", "extendX", "extendY" })
        Area(LatitudeLongitude center, int extendX, int extendY) {
            this.center = center;
            this.extendX = extendX;
            this.extendY = extendY;
        }
    }
}
