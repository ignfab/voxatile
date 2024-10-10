package com.ignfab.minalac.generator.processors;

import java.util.concurrent.atomic.AtomicBoolean;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.LASPointAndHeader;
import com.ignfab.minalac.generator.models.LASMergedModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Processor transforming {@link LASPointAndHeader LAS points} into {@link LASMergedModel LAS merged models}.
 * All points of a given class are accumulated in a single model, making this processor more suitable for large amount of points.
 * @see LASPointProcessor
 */
public class LASPointMergedModelProcessor extends ConvertingProcessor<LASPointAndHeader, LASMergedModel> {
    private final Int2ObjectMap<LASMergedModel> models = new Int2ObjectOpenHashMap<>();

    /**
     * Creates a new {@code LASPointMergedModelProcessor}.
     * @param converterProvider the converter provider to transform coordinates from map to world
     */
    public LASPointMergedModelProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
    }

    @Override
    public void initialize(CoordinateReferenceSystem layerCrs) throws GenerationFailedException {
        super.initialize(layerCrs);
        models.clear();
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
        AtomicBoolean created = new AtomicBoolean(false);
        LASMergedModel model = models.computeIfAbsent(object.point().getClassification(), classification -> {
            LASMergedModel m = new LASMergedModel();
            m.setMetadata("classification", classification);
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
