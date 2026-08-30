package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.ApplyShadingMinimapTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for {@link ApplyShadingMinimapTask}.
 */
public class ApplyShadingMinimapTaskParams extends TaskParams {

    /**
     * Name of the minimap to apply shading to (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String minimap;

    /**
     * Shadow intensity factor (optional, default is 0.5).
     *
     * <p>
     * A higher value will result in darker shadows. The value should be between 0 and 1.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double shadowIntensity = 0.5;

    /**
     * Sun azimuth in degrees (optional, default is 90).
     *
     * <ul>
     * <li>0 degrees is to the right (east)</li>
     * <li>90 degrees is up (north)</li>
     * <li>180 degrees is to the left (west)</li>
     * <li>270 degrees is down (south)</li>
     * </ul>
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double sunAzimuth = 90;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param minimap the name of the minimap to apply shading to
     */
    @ConstructorProperties({"minimap"})
    public ApplyShadingMinimapTaskParams(String minimap) {
        this.minimap = minimap;
    }

    @Override
    public void validate() {
        if (minimap.isEmpty() || minimap.isBlank())
            throw new IllegalArgumentException("Minimap name cannot be empty, or blank");
        if (shadowIntensity < 0 || shadowIntensity > 1)
            throw new IllegalArgumentException("Shadow intensity must be between 0 and 1");
        if (sunAzimuth < 0 || sunAzimuth > 360)
            throw new IllegalArgumentException("Sun azimuth must be between 0 and 360 degrees");
    }

    @Override
    public Task create(Generation generation) {
        return new ApplyShadingMinimapTask(generation.minimaps().get(minimap), shadowIntensity, Math.toRadians(sunAzimuth));
    }
}
