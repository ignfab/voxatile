package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;

/**
 * Represents the parameters of a type of {@link Heightmap}.
 */
public class HeightmapParams {
    /**
     * The default value for all heightmap cells.
     * This field is required.
     * When initialized by {@link HeightmapParams}:
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
     * @param defaultValue the default value for all heightmap cells. It must be an integer or one of the following: "minimal", "min", "maximal" or "max"
     * @throws IllegalArgumentException if {@code defaultValue} is not a valid integer string or is not "minimal", "min", "maximal" or "max"
     */
    @ConstructorProperties({"default"})
    public HeightmapParams(String defaultValue) throws ParseException {
        this.defaultValue = switch (defaultValue) {
            case "minimal", "min" -> Integer.MIN_VALUE;
            case "maximal", "max" -> Integer.MAX_VALUE;
            default -> {
                try {
                    yield Integer.parseInt(defaultValue);
                } catch (NumberFormatException e) {
                    throw new ParseException("Invalid heightmap default field value: " + defaultValue, e);
                }
            }
        };
    }

    /**
     * Checks if there are any blatantly invalid parameters.
     *
     * @throws IllegalArgumentException is any of the parameters is invalid.
     */
    public void validate() throws IllegalArgumentException {}

    /**
     * Creates the corresponding {@code Heightmap}.
     *
     * @param generation the generation context.
     * @return the corresponding heightmap
     */
    public Heightmap create(Generation generation) {
        return new Heightmap(generation.world().limits().to2d(), defaultValue);
    }
}
