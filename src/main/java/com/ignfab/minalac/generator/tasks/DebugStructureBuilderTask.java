package com.ignfab.minalac.generator.tasks;

import java.util.List;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class DebugStructureBuilderTask implements TileTask {
    private List<LayoutBuilder> builders;
    private WorldCoords3d place;
    private WorldSize2d size;

    public DebugStructureBuilderTask(List<LayoutBuilder> builders, WorldCoords3d place, WorldSize2d size) throws UnbuildableException {
        this.builders = builders;
        this.place = place;
        this.size = size;

        // We need X and Z axis to fill the whole build area. Y is left free.
        // TODO: Check that --> Could be made if build(dim)?
        for (LayoutBuilder builder: builders) {
            builder.axisX().makeAdjusted();
            builder.axisZ().makeAdjusted();
        }

    }

    @Override
    public void run(GenerationTile tile) {
        Structure structure = null;

        for (LayoutBuilder builder : builders) {
            System.out.println("Build atempt...");
            try {
                structure = builder.build(size.x(), null, size.y());
                break;
            } catch (UnbuildableException e) {}
        }

        if (structure == null) {
            // TODO: Throw exception ?
            System.out.println("Could not build facade structure");
            return;
        }

        structure.place(tile.voxels(), place.x(), place.y(), place.z());
    }
}
