package com.ignfab.minalac.generator.renderers;

import java.util.Random;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Ground renderer renders a basic ground using altitude from given heightmap.
 */
public class GroundRenderer implements Renderer {
    private final Seed seed;
    private final Heightmap heightmap;
    private final Placeable material;

    /**
     * Creates a new GroundRenderer.
     *
     * @param seed random seed for this renderer
     * @param heightmap heightmap of the ground
     * @param material material to be placed at each voxel of the given heightmap
     */
    public GroundRenderer(Seed seed, Heightmap heightmap, Placeable material) {
        this.seed = seed;
        this.heightmap = heightmap;
        this.material = material;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(WorldBBox3d bbox) {
        // TODO: Here random depends on area and so, it is not deterministic.
        // Some work has to be done to do tiled random generation.
        Random random = seed.createRandom();
        for (WorldCoords2d c : heightmap.bbox().intersection(bbox))
            material.place(random, c.x(), c.y(), heightmap.get(c));
    }
}
