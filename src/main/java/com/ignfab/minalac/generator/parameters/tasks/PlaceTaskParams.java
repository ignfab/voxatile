package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d.Voxelizer3dParams;
import com.ignfab.minalac.generator.tasks.PlaceTask;
import com.ignfab.minalac.generator.utils.execution.Task;

/**
 * Parameters for a {@link PlaceTask}.
 */
public class PlaceTaskParams extends ModelTaskParams {

    /**
     * Voxelizer to use to determine where to place (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public Voxelizer3dParams voxels;

    /**
     * Placeable to place at points (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param voxels where to place
     * @param place what to place
     */
    @ConstructorProperties({ "voxels", "place"})
    public PlaceTaskParams(Voxelizer3dParams voxels, PlaceableParams place) {
        this.voxels = voxels;
        this.place = place;
    }

    @Override
    public void validate() {
        super.validate();
        voxels.validate();
        place.validate();
    }

    @Override
    public Task create(Generation generation) {
        return new PlaceTask(
            models.create(),
            voxels.create(),
            place.create(generation.seed())
        );
    }

}
