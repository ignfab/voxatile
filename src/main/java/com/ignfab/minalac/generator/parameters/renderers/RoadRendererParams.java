package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.models.selection.ModelMetadataListSelection;
import com.ignfab.minalac.generator.models.selection.ModelMetadataSelection;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.renderers.RoadRenderer;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.SemanticType;
import com.ignfab.minalac.generator.world.SimpleVoxelPattern;
import com.ignfab.minalac.generator.world.VoxelType;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * Parameters for a {@link RoadRenderer}.
 * <p>
 * Until voxel structures are deserializable, this performs a basic voxel structure creation
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class RoadRendererParams extends RendererParams {
    /**
     * The type of models to render (required).
     */
    public String modelType;
    /**
     * The classifications to filter (temporary, optional, defaults to none).
     */
    public List<String> classifications;
    /**
     * The name of the heightmap to use (required).
     */
    public String heightmap;
    /**
     * Whether to create a large road (temporary, to be replaced with structures, then improved using real width).
     */
    public boolean large;
    /**
     * Voxel type to place (temporary, to be replaced with structures).
     */
    public SemanticType voxel;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param modelType the type of models to render
     * @param heightmap the name of the heightmap to use
     * @param voxel voxel to place
     */
    @ConstructorProperties({"modelType", "heightmap", "voxel"})
    public RoadRendererParams(String modelType, String heightmap, SemanticType voxel) {
        this.modelType = modelType;
        this.heightmap = heightmap;
        this.voxel = voxel;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (heightmap.isEmpty())
            throw new IllegalArgumentException("The field heightmap cannot be empty");
    }

    @Override
    public Renderer create(Generation generation) {
        SimpleVoxelPattern pattern = new SimpleVoxelPattern();
        VoxelType v = generation.world().getFactory().createVoxelType(voxel);
        if (large) {
            pattern.set(new WorldBBox3d(-1, -2, 0, 3, 1, 1), v);
            pattern.set(new WorldBBox3d(-2, -1, 0, 5, 3, 1), v);
            pattern.set(new WorldBBox3d(-1, +2, 0, 3, 1, 1), v);
        } else {
            pattern.set(new WorldBBox3d(-1, 0, 0, 3, 1, 1), v);
            pattern.set(new WorldBBox3d(0, -1, 0, 1, 3, 1), v);
        }

        ModelFilter models = new ModelSelection(generation.models(), modelType);
        if (classifications != null && !classifications.isEmpty()) {
            if (classifications.size() == 1)
                models = new ModelMetadataSelection(models, "classification", classifications.get(0));
            else
                models = new ModelMetadataListSelection(models, "classification", classifications);
        }
        return new RoadRenderer(
            models,
            generation.heightmaps().get(heightmap),
            pattern
        );
    }
}
