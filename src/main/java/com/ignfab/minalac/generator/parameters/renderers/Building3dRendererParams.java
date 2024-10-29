package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.Building3dRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.world.SemanticType;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:VisibilityModifier")
public class Building3dRendererParams extends RendererParams {
    public String modelType;
    public SemanticType ground;
    public SemanticType wall;
    public Map<String, SemanticType> roofs;
    public SemanticType defaultRoof;

    @ConstructorProperties({"modelType", "ground", "wall", "roofs", "defaultRoof"})
    public Building3dRendererParams(String modelType, SemanticType ground, SemanticType wall, Map<String, SemanticType> roofs, SemanticType defaultRoof) {
        this.modelType = modelType;
        this.ground = ground;
        this.wall = wall;
        this.roofs = roofs;
        this.defaultRoof = defaultRoof;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (modelType.isEmpty())
            throw new IllegalArgumentException("The field modelType cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        return new Building3dRenderer(
            new ModelSelection(generation.models(), modelType),
            generation.world().getFactory().createVoxelType(ground),
            generation.world().getFactory().createVoxelType(wall),
            roofs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> generation.world().getFactory().createVoxelType(entry.getValue()))),
            generation.world().getFactory().createVoxelType(defaultRoof)
        );
    }
}
