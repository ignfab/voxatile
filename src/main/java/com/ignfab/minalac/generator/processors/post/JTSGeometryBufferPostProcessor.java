package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;

/**
 * Post-processor working on {@link JTSGeometryModel}s
 * applying a buffer around a model's geometry.
 * The model's geometry is modified using the
 * {@link org.locationtech.jts.geom.Geometry#buffer(double) .buffer(d)}
 * JTS method on the geometry with a buffer value {@code d}.
 * <p>
 * Models resulting in empty geometries can be discarded.
 * <p>
 * The same model object is returned after post-processing.
 */
public class JTSGeometryBufferPostProcessor extends PostProcessor.Simple<JTSGeometryModel> {
    private final double buffer;
    private final boolean discardEmptyResults;

    /**
     * Creates a new {@code JTSGeometryBufferPostProcessor}.
     * @param buffer The buffer distance value to apply
     * @param discardEmptyResults Whether to discard models resulting in empty geometry
     */
    public JTSGeometryBufferPostProcessor(double buffer, boolean discardEmptyResults) {
        super(JTSGeometryModel.class);
        this.buffer = buffer;
        this.discardEmptyResults = discardEmptyResults;
    }

    @Override
    public JTSGeometryModel process(JTSGeometryModel model) throws GenerationFailedException, IgnorableException {
        if (buffer != 0)
            model.setGeometry(model.getGeometry().buffer(buffer));
        if (discardEmptyResults && model.getGeometry().isEmpty())
            return null;
        return model;
    }
}
