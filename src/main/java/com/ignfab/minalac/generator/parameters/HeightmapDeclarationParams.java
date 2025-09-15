package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;

/**
 * Parameters for a {@link HeightmapDeclaration}.
 */
public class HeightmapDeclarationParams implements Params {
    /**
     * The default value for all heightmap cells.
     * This field is required.
     * When initialized by {@link HeightmapDeclarationParams}:
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
    public HeightmapDeclarationParams(String defaultValue) throws ParseException {
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
     * Creates the corresponding {@code Heightmap} declaration.
     *
     * @param name name of the stored heightmap
     * @return the corresponding heightmap declaration
     */
    public HeightmapDeclaration create(String name) {
        return new HeightmapDeclaration(name, defaultValue);
    }
}
