package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.HeightMap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Rasterizable;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;

/**
 * HeightMap renderer copies data from given models to a heightmap.
 * If data is overlapping, only the last information in the iterator order is kept.
 */
public class HeightMapRenderer {
    private HeightMap heightMap;
    private Iterable<Model> models;

    /**
     * Creates a new HeightMapRenderer.
     *
     * @param heightMap Height map of the ground (where heights will be written)
     * @param models Models to be rendered (only Rasterizable ones will be)
     */
    public HeightMapRenderer(HeightMap heightMap, Iterable<Model> models) {
        this.heightMap = heightMap;
        this.models = models;
    }

    /**
     * Performs rendering.
     */
    public void render() {
        for (Model model : models) {
            if (!(model instanceof Rasterizable rasterizable)) {
                // TODO: Better warning about not possible to render a non rasterizable model
                System.out.println("Ignoring non rasterizable model.");
                continue;
            }

            if (!(rasterizable.getChunk() instanceof IterableChunk2d chunk)) {
                // TODO: Better warning about not possible to render a non iterable model
                System.out.println("Ignoring non iterable chunk.");
                continue;
            }

            // Iterate over chunk and draw shape on map at heightMap altitude
            for (Chunk2dElement element : chunk) {
                WorldCoords2d c = element.getCoords();
                // TODO: Make Iterator able to intersect with another box
                // TODO: Have a world bbox rather
                if (heightMap.bbox().contains(c)) {
                    heightMap.set(c, element.getValue());
                }
            }
        }
    }
}
