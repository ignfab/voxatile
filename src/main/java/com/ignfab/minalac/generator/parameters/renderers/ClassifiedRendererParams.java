package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.NoVoxelParams;
import com.ignfab.minalac.generator.renderers.ClassifiedRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * Parameters for a {@link ClassifiedRenderer}.
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ClassifiedRendererParams extends RendererParams {
    /**
     * The models to render.
     * This field is required during deserialization.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;
    /**
     * List of placeables for each class.
     * This field is required during deserialization.
     */
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public Map<String, PlaceableParams> classes;
    /**
     * Default placeable if no class matches.
     * Optional (defaults to no voxel).
     */
    @JsonProperty("default")
    @JsonSetter(nulls = Nulls.SKIP)
    public PlaceableParams defaultPlace = new NoVoxelParams();

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models the models to render
     * @param classes placeables to place for each class
     */
    @ConstructorProperties({"models", "classes"})
    public ClassifiedRendererParams(ModelSelectionParams models, Map<String, PlaceableParams> classes) {
        this.models = models;
        this.classes = classes;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
        classes.values().forEach(PlaceableParams::validate);
        defaultPlace.validate();
    }

    @Override
    public Renderer create(Generation generation) {
        return new ClassifiedRenderer(
            models.create(generation.models()),
            classes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().create(generation.world()))),
            defaultPlace.create(generation.world())
        );
    }
}
