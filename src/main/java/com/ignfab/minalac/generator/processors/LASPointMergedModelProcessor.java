package com.ignfab.minalac.generator.processors;

import com.github.mreutegg.laszip4j.LASHeader;
import com.github.mreutegg.laszip4j.LASPoint;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASMergedModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class LASPointMergedModelProcessor extends ConvertingProcessor<LASPointAndHeader, LASMergedModel> {
    private LASMergedModel model;

    public LASPointMergedModelProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
    }

    @Override
    public Class<LASPointAndHeader> acceptedType() {
        return LASPointAndHeader.class;
    }

    @Override
    public Class<LASMergedModel> modelType() {
        return LASMergedModel.class;
    }

    @Override
    public LASMergedModel process(LASPointAndHeader object) throws GenerationFailedException, IgnorableException {
        if (model == null)
            model = new LASMergedModel();

        WorldCoords3d coords;
        try {
            LASHeader header = object.header();
            LASPoint point = object.point();
            coords = converter.convert(new MapCoordinates(
                header.getXOffset() + point.getX() * header.getXScaleFactor(),
                header.getYOffset() + point.getY() * header.getYScaleFactor()
            )).to3d((int) Math.round(header.getZOffset() + point.getZ() * header.getZScaleFactor()));
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
        model.addPoint(coords);

        if (object.last()) {
            LASMergedModel m = model;
            model = null;
            return m;
        }
        return null;
    }
}
