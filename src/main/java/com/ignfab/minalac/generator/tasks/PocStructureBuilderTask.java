package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class PocStructureBuilderTask implements TileTask {
    private ReadableHeightmapSpec minimum;
    private ResizedStructureBuilder resizedBuilder;
    private Placeable fallback;
    private WorldCoords3d place;
    private WorldSize3d size;

    public PocStructureBuilderTask(ReadableHeightmapSpec minimum, ResizedStructureBuilder resizedBuilder, Placeable fallback, WorldCoords3d place, WorldSize3d size) {
        this.minimum = minimum;
        this.resizedBuilder = resizedBuilder;
        this.fallback = fallback;
        this.place = place;
        this.size = size;
    }

    @Override
    public void run(GenerationTile tile) {
        Placeable toPlace;
        try {
            int sizeX = resizedBuilder.axisX().maxSizeUnder(size.x());
            int sizeY = resizedBuilder.axisY().maxSizeUnder(size.y());
            int sizeZ = resizedBuilder.axisZ().maxSizeUnder(size.z());
            System.out.println(resizedBuilder.axisX());
            System.out.println(sizeX + ", " + sizeY + ", " + sizeZ);

            toPlace = resizedBuilder.build(sizeX, sizeY, sizeZ);

        } catch (UnresizableStructureException e) {
            toPlace = fallback;
        }

        toPlace.place(tile.voxels(), place.x(), place.y(), tile.heightmaps().get(minimum).get(place.x(), place.y()) + place.z());
    }
}
