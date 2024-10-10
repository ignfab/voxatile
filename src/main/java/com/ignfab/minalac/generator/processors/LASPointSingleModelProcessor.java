package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASSingleModel;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class LASPointSingleModelProcessor implements Processor<LASPointAndHeader, LASSingleModel> {
    private final MapToWorldConverter converter;
    private LASSingleModel model;

    public LASPointSingleModelProcessor(MapToWorldConverter converter) {
        this.converter = converter;
    }

    @Override
    public Class<LASPointAndHeader> acceptedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Class<LASSingleModel> modelType() {
        return LASSingleModel.class;
    }

    @Override
    public LASSingleModel process(LASPointAndHeader object) throws GenerationFailedException, IgnorableException {
        if (model == null)
            model = new LASSingleModel();

        WorldCoords3d coords;
        try {
            coords = converter.convert(new MapCoordinates(
                object.header().getXOffset() + object.point().getX() * object.header().getXScaleFactor(),
                object.header().getYOffset() + object.point().getY() * object.header().getYScaleFactor()
            )).to3d((int) Math.round(object.header().getZOffset() + object.point().getZ() * object.header().getZScaleFactor()));
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
        model.addPoint(coords);

        if (object.last()) {
            LASSingleModel m = model;
            model = null;
            return m;
        }
        return null;
    }
}
