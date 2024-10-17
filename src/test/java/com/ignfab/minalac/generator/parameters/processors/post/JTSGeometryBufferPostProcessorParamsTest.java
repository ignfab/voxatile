package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.processors.post.JTSGeometryBufferPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class JTSGeometryBufferPostProcessorParamsTest {
    @Test
    @DisplayName("Simple JTS geometry buffer post-processor params is valid")
    public void testValidateValid() {
        JTSGeometryBufferPostProcessorParams valid = new JTSGeometryBufferPostProcessorParams(7);
        assertDoesNotThrow(valid::validate);
    }

    @Test
    @DisplayName("JTS geometry buffer post-processor params with an invalid buffer is invalid")
    public void testValidateInvalid() {
        JTSGeometryBufferPostProcessorParams invalid = new JTSGeometryBufferPostProcessorParams(Double.NaN);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    @DisplayName("JTS geometry buffer post-processor params creates a JTS geometry post-processor")
    public void testCreate() {
        JTSGeometryBufferPostProcessorParams params = new JTSGeometryBufferPostProcessorParams(-2);
        params.discardEmptyResults = true;
        PostProcessor<?, ?> postProcessor = assertDoesNotThrow(params::create);
        assertInstanceOf(JTSGeometryBufferPostProcessor.class, postProcessor);
    }
}
