package com.ignfab.minalac.generator.parameters.tasks;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.tasks.ExtendModelTypeVolumeTask;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link ExtendModelTypeVolumeTask}.
 */
public class ExtendModelTypeVolumeTaskParams extends TileTaskParams {

    /**
     * List of model types which volume is to extend (required)
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<String> extendVolumeOf;

    /**
     * List of model selections of which include volumes (required)
     */
    @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ModelSelectionParams> includeVolumeOf;

    @Override
    public void validate() {
        for (ModelSelectionParams params: includeVolumeOf)
            params.validate();

        for (String name: extendVolumeOf)
            if (name.isBlank())
                throw new IllegalArgumentException("Model type name cannot be blank");
    }

    @Override
    public TileTask create(Generation generation) {
        List<ModelSelection> selections = new LinkedList<>();
        for (ModelSelectionParams params: includeVolumeOf)
            selections.add(params.create());

        return new ExtendModelTypeVolumeTask(extendVolumeOf, selections);
    }

}