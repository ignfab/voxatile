package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.VectorRenderer;
import com.ignfab.minalac.generator.world.SemanticType;

import java.beans.ConstructorProperties;

/**
 * Concrete class of {@link RendererParams} representing the parameters of a {@link VectorRenderer}.
 */
// Since attributes are purposely kept public for this class the checkstyle for visibility is disabled.
@SuppressWarnings("checkstyle:VisibilityModifier")
public class VectorRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The name of the ground heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;
    // TODO: SemanticType should be replaced when placeable interface along its deserialization is implemented
    /**
     * The semantic type of voxel used to render the inside of the geometry of the models.
     * This field is required during deserialization.
     */
    public SemanticType inside;
    /**
     * The semantic type of voxel used to render the outside of the geometry of the models.
     * This field is required during deserialization.
     */
    public SemanticType edge;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the ground heightmap to use.
     * @param inside the voxel used for the inside
     * @param edge the voxel used for the edges
     */
    @ConstructorProperties({"modelType", "heightmap", "inside", "edge"})
    public VectorRendererParams(String modelType, String heightmap, SemanticType inside, SemanticType edge) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.inside = inside;
        this.edge = edge;
    }

    /**
     * {@inheritDoc}
     */
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Renderer create(Generation generation) {
        return new VectorRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.heightmaps().get(heightmap),
            generation.world().getFactory().createVoxelType(inside),
            generation.world().getFactory().createVoxelType(edge)
        );
    }
}
