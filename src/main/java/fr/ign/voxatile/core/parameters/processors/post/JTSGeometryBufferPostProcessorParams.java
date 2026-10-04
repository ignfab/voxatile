package fr.ign.voxatile.core.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.JTSGeometryModel;
import fr.ign.voxatile.core.processors.post.JTSGeometryBufferPostProcessor;
import fr.ign.voxatile.core.processors.post.PostProcessor;

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
