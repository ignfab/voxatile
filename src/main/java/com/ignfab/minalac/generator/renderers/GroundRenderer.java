package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.Placer;
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
     * @param seed random seed, not used by renderer but passed to placeables
     * @param heightmap heightmap of the ground
     * @param material material to be placed at each voxel of the given heightmap
     */
    public GroundRenderer(Seed seed, Heightmap heightmap, Placeable material) {
        this.seed = seed.salt("GroundRenderer");
        this.heightmap = heightmap;
        this.material = material;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(WorldBBox3d bbox) {
        Placer material = this.material.placer(seed, null);
        // TODO: Needs a raster salt mechanism
        for (WorldCoords2d c : heightmap.bbox().intersection(bbox))
            material.place(c.x(), c.y(), heightmap.get(c));
    }

}
