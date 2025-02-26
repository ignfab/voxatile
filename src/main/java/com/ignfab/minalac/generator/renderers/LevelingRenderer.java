package com.ignfab.minalac.generator.renderers;

import java.util.Random;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * Alters floor level to guarantee flat surface under each model.
 * The final floor height is equals to the greatest floor height over all the
 * model's surface.
 */
public class LevelingRenderer extends ModelRenderer<Voxelizable2d> {
    /**
     * Heightmap of the ground, will be updated according to leveling.
     */
    private final Heightmap heightmap;

    /**
     * {@code Placeable} used to fill the space beneath the building geometry,
     * ensuring it connects to the ground and doesn't appear to float.
     */
    private final Placeable filling;

    /**
     * Creates a new LevelingRenderer.
     *
     * @param seed random seed for this renderer
     * @param selection models selection to be leveled
     * @param heightmap heightmap of the ground
     * @param filling {@code Placeable} for fill leveled areas with
     */
    public LevelingRenderer(Seed seed, ModelSelection selection, Heightmap heightmap, Placeable filling) {
        super(seed, Voxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.filling = filling;
    }

    @Override
    protected void render(Seed seed, Voxelizable2d model, WorldBBox3d bbox) {
        Random random = seed.createRandom();

        Voxelizer2d voxelizer = model.voxelize2d(bbox.to2d());

        // The highest coordinate of the model.
        int zMax = Integer.MIN_VALUE;
        for (Positioned2d voxel : voxelizer)
            zMax = Math.max(zMax, heightmap.get(voxel.coords()));

        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            // Flatten the floor for the model by placing filling voxels up to zMax.
            for (int z = heightmap.get(c); z <= zMax; z++)
                filling.place(random, c.x(), c.y(), z);

            // Update the heightmap to reflect the leveling
            // and ensure the model is positioned above the new floor level.
            heightmap.set(c, zMax);
        }
    }
}
