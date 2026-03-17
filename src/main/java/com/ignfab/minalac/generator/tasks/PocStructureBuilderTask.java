package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class PocStructureBuilderTask implements TileTask {
    private ReadableHeightmapSpec minimum;
    private LayoutBuilder layoutBuilder;
    private Placeable fallback;
    private WorldCoords3d place;
    private WorldSize3d size;

    public PocStructureBuilderTask(ReadableHeightmapSpec minimum, LayoutBuilder layoutBuilder, Placeable fallback, WorldCoords3d place, WorldSize3d size) {
        this.minimum = minimum;
        this.layoutBuilder = layoutBuilder;
        this.fallback = fallback;
        this.place = place;
        this.size = size;
    }

    @Override
    public void run(GenerationTile tile) {
        Placeable toPlace;
        try {
            int sizeX = layoutBuilder.axisX().maxSizeUnder(size.x());
            int sizeY = layoutBuilder.axisY().maxSizeUnder(size.y());
            int sizeZ = layoutBuilder.axisZ().maxSizeUnder(size.z());
            System.out.println(layoutBuilder.axisX());
            System.out.println(sizeX + ", " + sizeY + ", " + sizeZ);

            toPlace = layoutBuilder.build(sizeX, sizeY, sizeZ);

        } catch (UnbuildableException e) {
            toPlace = fallback;
        }

        toPlace.place(tile.voxels(), place.x(), place.y(), tile.heightmaps().get(minimum).get(place.x(), place.y()) + place.z());
    }
}
