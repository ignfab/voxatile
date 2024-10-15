package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.renderers.WaterRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.world.SemanticType;

import java.beans.ConstructorProperties;

/**
 *  Parameters for a {@link WaterRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class WaterRendererParams extends RendererParams {
    /**
     * The name of the ground heightmap to use.
     * This field is required during deserialization.
     */
    public String groundHeightmap;
    /**
     * The name of the water heightmap to use.
     * This field is required during deserialization.
     */
    public String waterHeightmap;
    /**
     * The semantic type of voxel used to render the water.
     * This field is optional. (Default value: WATER)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public SemanticType waterVoxel = SemanticType.WATER;
    /**
     * The semantic type of voxel used to render the gap between water and ground surfaces.
     * This field is optional. (Default value: AIR)
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public SemanticType airVoxel = SemanticType.AIR;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param groundHeightmap the name of the ground heightmap to use.
     * @param waterHeightmap the name of the water heightmap to use.
     */
    @ConstructorProperties({"groundHeightmap", "waterHeightmap"})
    public WaterRendererParams(String groundHeightmap, String waterHeightmap) {
        this.groundHeightmap = groundHeightmap;
        this.waterHeightmap = waterHeightmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Renderer create(Generation generation) {
        return new WaterRenderer(
            generation.heightmaps().get(groundHeightmap),
            generation.heightmaps().get(waterHeightmap),
            generation.world().getFactory().createVoxelType(waterVoxel),
            generation.world().getFactory().createVoxelType(airVoxel)
        );
    }
}
