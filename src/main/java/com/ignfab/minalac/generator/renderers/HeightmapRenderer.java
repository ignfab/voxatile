package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;

/**
 * Heightmap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class HeightmapRenderer {
    private Heightmap heightmap;
    private Iterable<Model> models;

    /**
     * Creates a new HeightmapRenderer.
     *
     * @param heightmap Heightmap of the ground (where heights will be written)
     * @param models Models to be rendered (only {@code FloatMatrixModel} will be)
     */
    public HeightmapRenderer(Heightmap heightmap, Iterable<Model> models) {
        this.heightmap = heightmap;
        this.models = models;
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    public void render(WorldBBox3d bbox) {
        for (Model model : models) {
            if (!(model instanceof FloatMatrixModel matrix)) {
                // TODO: Better warning about not possible to render a non float matrix model
                System.out.println("Ignoring non float matrix model.");
                continue;
            }

            // Iterate over matrix and fill heightmap altitude
            for (Matrix2d.Value<Float> value : matrix) {
                WorldCoords2d c = value.coords();
                if (heightmap.bbox().contains(c)) {
                    heightmap.set(c, (int) Math.round(value.value()));
                }
            }
        }
    }
}
