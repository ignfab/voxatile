package com.ignfab.minalac.generator.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final Map<String, LASSingleModel> models = new HashMap<>();

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
        String classification = Short.toString(object.point().getClassification());
        AtomicBoolean created = new AtomicBoolean(false);
        LASSingleModel model = models.computeIfAbsent(classification, cls -> {
            LASSingleModel m = new LASSingleModel();
            m.setMetadata("classification", cls);
            created.set(true);
            return m;
        });
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

        return created.get() ? model : null;
    }
}
