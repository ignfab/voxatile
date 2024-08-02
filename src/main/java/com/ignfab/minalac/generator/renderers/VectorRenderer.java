package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.HeightMap;
import com.ignfab.minalac.generator.models.GeometryModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Rasterizable;
import com.ignfab.minalac.generator.utils.world2d.chunk.IterableChunk2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.world.VoxelType;
import com.ignfab.minalac.generator.world.VoxelTypeIgnore;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class VectorRenderer {

    private HeightMap heightMap;
    private Iterable<Model> models;

    // This may evolve in something more elaborated, like edge and inside voxel patterns,
    // or even closures (one for inside, one for edge and another for init)
    private VoxelType inside;
    private VoxelType edge;
    private static final VoxelType IGNORE = new VoxelTypeIgnore();

    /**
     * Creates a new VectorRenderer.
     *
     * @param heightMap Height map of the ground (on which features will be placed)
     * @param models Models to be rendered (only Rasterizable ones will be)
     * @param inside Voxel type to draw inside geometries
     * @param edge Voxel type to draw on edges of geometries (including points and lines)
     */
    public VectorRenderer(HeightMap heightMap, Iterable<Model> models, VoxelType inside, VoxelType edge) {
        this.heightMap = heightMap;
        this.models = models;
        this.inside = inside;
        this.edge = edge;
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
                // TODO: Would be much better to intersect feature and word bbox rather.
                if (heightMap.bbox().contains(c)) {
                    VoxelType vt = switch (element.getValue()) {
                        case GeometryModel.INSIDE -> inside;
                        case GeometryModel.BORDER -> edge;
                        default -> IGNORE; // Should never get there
                    };
                    vt.place(c.x(), c.y(), heightMap.get(c) + 1);
                }
            }
        }
    }
}
