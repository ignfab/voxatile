package com.ignfab.minalac.generator.tasks;

import java.util.List;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

public class DebugStructureBuilderTask implements TileTask {
    private List<LayoutBuilder> builders;
    private WorldCoords3d place;
    private WorldSize3d size;

    public DebugStructureBuilderTask(List<LayoutBuilder> builders, WorldCoords3d place, WorldSize3d size) {
        this.builders = builders;
        this.place = place;
        this.size = size;
    }

    @Override
    public void run(GenerationTile tile) {
        Structure structure = null;

        for (LayoutBuilder builder : builders) {
            try {
                structure = builder.build(size.x(), size.y(), size.z());
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
