package com.ignfab.minalac.generator.tasks;

import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class ExtendModelTypeVolumeTask implements TileTask {
    private List<String> types;
    private List<ModelSelection> selections;

    public ExtendModelTypeVolumeTask(List<String> types, List<ModelSelection> selections) {
        this.types = types;
        this.selections = selections;
    }

    @Override
    public void run(GenerationTile tile) {
        for (ModelSelection selection : selections) {
            for (Model model: selection.forTile(tile)) {
                WorldBBox3d volume = null;               //TODO: COMPUTE MODEL BBOX
                // needs voxelization algorithm
                for (String type: types)
                    tile.modelTypeVolume(type).include(volume);
            }
        }
    }
}
