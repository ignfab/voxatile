package com.ignfab.minalac.generator.utils.paramsparser;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.beans.ConstructorProperties;

/**
 * RawParams is a POJO representing the parameters used during the generation.
 * This class mirrors the structure of the object to be deserialized.
 * Required fields are initialized by the constructor.
 * The verification of their presence is done by the constructor via the {@code @ConstructorProperties} annotation, as it is currently the only supported method by the library.
 * @see <a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/625">GitHub issue about required fields during deserialization</a>.
 */
//Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawParams {
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
//Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
class Area {
    /**
     * The center of the area represented by a POJO.
     * This field is required during deserialization.
     */
    public Center center;
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
     * @param center  the center of the area represented by a POJO.
     * @param extendX the extends in voxel along the x-axis.
     * @param extendY the extends in voxel along the y-axis.
     */
    @ConstructorProperties({ "center", "extendX", "extendY" })
    Area(Center center, int extendX, int extendY) {
        this.center = center;
        this.extendX = extendX;
        this.extendY = extendY;
    }
}
//Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
class Center {
    /**
     * The latitude of the center.
     */
    public double latitude;
    /**
     * The longitude of the center.
     */
    public double longitude;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param latitude  the latitude of the center.
     * @param longitude the latitude of the center.
     */
    @ConstructorProperties({"latitude", "longitude"})
    Center(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}

