package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public class ComputeMargins extends ModelTask<Shape2dConvertibleModel> {

    private final WorldBBox2d margins;
    private final String modelType;

    protected ComputeMargins(ModelSelection selection, WorldBBox2d margins, String modelType) {
        super(Shape2dConvertibleModel.class, selection);
        this.margins = margins;
        this.modelType = modelType;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        tile.includeBBoxForModelType(modelType, model.toShape2d().bbox().enlarged(margins));
    }
}
