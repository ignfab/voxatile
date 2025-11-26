package com.ignfab.minalac.generator.tasks;

import java.util.Iterator;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.Minimap;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.PlacedVoxel;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * Un {@code TileTask} permettant de definir le voxel le plus haut pour chaque coordonnée dans la tuile.
 */
public class PopulateMinimapTask implements TileTask {

    @Override
    public void run(GenerationTile tile) {
        Minimap minimap = tile.generation().minimap();
        Placeable ignore = tile.generation().world().defaultVoxel();
        VoxelTile voxels = tile.voxels();
        WorldBBox3d limits = voxels.limits();

        for (int xi = limits.minX(); xi < limits.maxX(); xi++) {
            for (int yi = limits.minY(); yi < limits.maxY(); yi++) {
                Iterator<PlacedVoxel> iterator = voxels.voxelIterator(xi, yi);

                while (iterator.hasNext()) {
                    PlacedVoxel voxel = iterator.next();
                    if (!voxel.voxel().equals(ignore)) {
                        minimap.set(voxel.coords());
                        break;
                    }
                }
            }
        }
    }
}
