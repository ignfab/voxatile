package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.ClassifiedRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.VoxelTypeIgnore;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parameters for a {@link ClassifiedRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ClassifiedRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * List of voxel types for each class.
     * This field is required during deserialization.
     */
    public Map<String, SemanticType> voxels;
    /**
     * Default voxel if no class matches.
     * Optional (defaults to no voxel).
     */
    public SemanticType defaultVoxel;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render
     * @param voxels voxels to place for each class
     */
    @ConstructorProperties({"modelType", "voxels"})
    public ClassifiedRendererParams(String modelType, Map<String, SemanticType> voxels) {
        this.modelType = modelType;
        this.voxels = voxels;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new ClassifiedRenderer(
            new ModelSelection(generation.models(), modelType),
            voxels.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> generation.world().getFactory().createVoxelType(entry.getValue()))),
            defaultVoxel == null ? new VoxelTypeIgnore() : generation.world().getFactory().createVoxelType(defaultVoxel)
        );
    }
}
