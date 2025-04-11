package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
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
    private final UnboundHeightmap heightmap;

    /**
     * {@code Placeable} used to fill the space beneath the building geometry,
     * ensuring it connects to the ground and doesn't appear to float.
     */
    private final Placeable filling;

    /**
     * Creates a new LevelingRenderer.
     *
     * @param selection models selection to be leveled
     * @param heightmap heightmap of the ground
     * @param filling {@code Placeable} for fill leveled areas with
     */
    public LevelingRenderer(ModelSelection selection, UnboundHeightmap heightmap, Placeable filling) {
        super(Voxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.filling = filling;
    }

    @Override
    protected void render(Voxelizable2d model, GenerationTile tile) {
        Voxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());
        Heightmap heightmap = this.heightmap.bind(tile);

        // The highest coordinate of the model.
        int zMax = Integer.MIN_VALUE;
        for (Positioned2d voxel : voxelizer)
            zMax = Math.max(zMax, heightmap.get(voxel.coords()));

        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            // Flatten the floor for the model by placing filling voxels up to zMax.
            for (int z = heightmap.get(c); z <= zMax; z++)
                filling.place(tile.worldTile(), c.x(), c.y(), z);

            // Update the heightmap to reflect the leveling
            // and ensure the model is positioned above the new floor level.
            heightmap.set(c, zMax);
        }
    }
}
