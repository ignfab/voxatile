package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.models.JTSGeometryModel;

/**
 * Post-processor working on {@link JTSGeometryModel}s
 * applying a buffer around a model's geometry.
 * The model's geometry is modified using the
 * {@link org.locationtech.jts.geom.Geometry#buffer(double) .buffer(d)}
 * JTS method on the geometry with a buffer value {@code d}.
 * <p>
 * The same model object is returned after post-processing.
 */
public class JTSGeometryBufferPostProcessor extends PostProcessor.Simple<JTSGeometryModel> {
    private final double buffer;

    /**
     * Creates a new {@code JTSGeometryBufferPostProcessor}.
     * @param buffer The buffer distance value to apply
     */
    public JTSGeometryBufferPostProcessor(double buffer) {
        super(JTSGeometryModel.class);
        this.buffer = buffer;
    }

    @Override
    public JTSGeometryModel process(JTSGeometryModel model) {
        if (buffer != 0)
            model.setGeometry(model.getGeometry().buffer(buffer));
        return model;
    }
}
