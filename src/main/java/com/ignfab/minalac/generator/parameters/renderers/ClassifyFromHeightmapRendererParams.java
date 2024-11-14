package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.models.selection.ModelMetadataListSelection;
import com.ignfab.minalac.generator.models.selection.ModelMetadataSelection;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.ClassifyFromHeightmapRenderer;
import com.ignfab.minalac.generator.renderers.Renderer;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

@SuppressWarnings("checkstyle:VisibilityModifier")
public class ClassifyFromHeightmapRendererParams extends RendererParams {
    /**
     * The type of models to render.
     * This field is required during deserialization.
     */
    public String modelType;
    /**
     * The classifications to filter (temporary, optional, defaults to none).
     */
    public List<String> classifications;
    /**
     * The name of the heightmap to use.
     * This field is required during deserialization.
     */
    public String heightmap;
    public Map<Integer, String> classes;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render.
     * @param heightmap the name of the heightmap to use.
     * @param classes the mapping from heightmap to classification.
     */
    @ConstructorProperties({"modelType", "heightmap", "classes"})
    public ClassifyFromHeightmapRendererParams(String modelType, String heightmap, Map<Integer, String> classes) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.classes = classes;
    }

    @Override
    public Renderer create(Generation generation) {
        ModelFilter models = new ModelSelection(generation.models(), modelType);
        if (classifications != null && !classifications.isEmpty()) {
            if (classifications.size() == 1)
                models = new ModelMetadataSelection(models, "classification", classifications.get(0));
            else
                models = new ModelMetadataListSelection(models, "classification", classifications);
        }
        return new ClassifyFromHeightmapRenderer(
            models,
            generation.heightmaps().get(heightmap),
            classes
        );
    }
}
