package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
import com.ignfab.minalac.generator.tasks.SetHeightmapTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link SetHeightmapTask}.
 */
public class SetHeightmapTaskParams extends TileTaskParams {
    /**
     * Models to use as a filter (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelSelectionParams models;

    /**
     * Value to set (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams set;

    /**
     * The name of the heightmap receiving the values (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public WritableHeightmapParams to;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models under which set the value
     * @param set value to set
     * @param to the name of the heightmap receiving the value
     */
    @ConstructorProperties({"models", "set", "to"})
    public SetHeightmapTaskParams(ModelSelectionParams models, ModelValueParams set, WritableHeightmapParams to) {
        this.models = models;
        this.set = set;
        this.to = to;
    }

    @Override
    public void validate() {
        models.validate();
        set.validate();
        to.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new SetHeightmapTask(
            models.create(),
            set.create(generation),
            to.create(generation.heightmaps())
        );
    }
}
