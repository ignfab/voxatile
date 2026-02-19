package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class TestingStructureBuilderTask implements TileTask {
    private ReadableHeightmapSpec minimum;
    private ResizedStructureBuilder resizedBuilder;
    private Placeable fallback;

    public TestingStructureBuilderTask(ReadableHeightmapSpec minimum, ResizedStructureBuilder resizedBuilder, Placeable fallback) {
        this.minimum = minimum;
        this.resizedBuilder = resizedBuilder;
        this.fallback = fallback;
    }

    @Override
    public void run(GenerationTile tile) {
        int w = 100;
        int p = 1;
        int h = 100;

        Placeable toPlace;
        try {

            int sizeX = resizedBuilder.axisX().maxSizeUnder(w);
            int sizeY = resizedBuilder.axisY().maxSizeUnder(p);
            int sizeZ = resizedBuilder.axisZ().maxSizeUnder(h);
            System.out.println(resizedBuilder.axisX());
            System.out.println(sizeX + ", " + sizeY + ", " + sizeZ);

            toPlace = resizedBuilder.build(sizeX, sizeY, sizeZ);

        } catch (UnresizableStructureException e) {
            toPlace = fallback;
        }

        int x = 10;
        int y = 10;
        int z = 5;

        toPlace.place(tile.voxels(), x, y, tile.heightmaps().get(minimum).get(x, y) + z);
    }
}
