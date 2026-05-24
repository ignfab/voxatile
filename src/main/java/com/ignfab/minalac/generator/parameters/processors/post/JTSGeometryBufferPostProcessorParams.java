package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.processors.post.JTSGeometryBufferPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link JTSGeometryBufferPostProcessor}.
 */
public class JTSGeometryBufferPostProcessorParams extends PostProcessorParams {
    /**
     * Buffer distance value to apply (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double buffer;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     * @param buffer The buffer value
     */
    @ConstructorProperties({ "buffer" })
    public JTSGeometryBufferPostProcessorParams(double buffer) {
        this.buffer = buffer;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (!Double.isFinite(buffer))
            throw new IllegalArgumentException("The 'buffer' field must be a finite decimal number");
    }

    @Override
    public PostProcessor<JTSGeometryModel, ?> create(Generation generation) {
        return new JTSGeometryBufferPostProcessor(buffer);
    }
}
