package com.ignfab.minalac.generator.parsing;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * RawParams is a POJO representing the parameters used during the generation.
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
public class RawParams {
    /**
     * The list of heightmaps used during the generation.
     * This field is optional.
     */
    public List<HeightMapParams> heightMaps;

    public static class HeightMapParams {
        /**
         * The name of the heightmap.
         * The name must be unique.
         * This field is required.
         */
        public String name;
        /**
         * The default value for all height map cells.
         * This field is required.
         * When initialized by {@link HeightMapParams}:
         * <ul>
         *     <li>"minimal" and "min" are transformed to {@link Integer#MIN_VALUE}</li>
         *     <li>"maximal" and "max" are transformed to {@link Integer#MAX_VALUE}</li>
         * </ul>
         */
        public int defaultValue;

        /**
         * Constructor used to ensure that the required fields are present during deserialization.
         * The {@code defaultValue} must be a valid string integer or one of the following: "minimal", "min", "maximal" or "max".
         * When parsed "minimal" or "min" will be transformed to {@link Integer#MIN_VALUE}
         * and "maximal" or "max" will be transformed to {@link Integer#MAX_VALUE}.
         *
         * @param name         the name of the heightmap.
         * @param defaultValue the default value for all heightmap cells. It must be an integer or one of the following: "minimal", "min", "maximal" or "max"
         * @throws ParseException if {@code defaultValue} is not a valid integer string or is not "minimal", "min", "maximal" or "max"
         */
        @ConstructorProperties({"name", "default"})
        public HeightMapParams(String name, String defaultValue) throws ParseException {
            this.name = name;
            this.defaultValue = parseDefaultValue(defaultValue);
        }

        private int parseDefaultValue(String defaultValue) throws ParseException {
            return switch (defaultValue) {
                case "minimal", "min" -> Integer.MIN_VALUE;
                case "maximal", "max" -> Integer.MAX_VALUE;
                default -> {
                    try {
                        yield Integer.parseInt(defaultValue);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid heightmap default field value : " + defaultValue, e);
                    }
                }
            };
        }
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
             */
            public double latitude;
            /**
             * Longitude.
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

    // For now :
    // - field mapName is not yet implemented (should probably be)
    /**
     * Vertical scale (vertical size of voxel in meters).
     * This field is optional. (Default value : 1.0)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Double verticalScale = 1.0;
    /**
     * The horizontal scale (horizontal size of voxel in meters).
     * This field is optional. (Default value : 1.0)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Double horizontalScale = 1.0;
    /**
     * The area of generation represented by a POJO.
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
     * The format of the generated map.
     * This field is required.
     */
    public String format;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param area the area of generation represented by a POJO.
     * @param format the format of the game.
     */
    @ConstructorProperties({"area", "format"})
    public RawParams(Area area, String format) {
        this.area = area;
        this.format = format;
    }
}
