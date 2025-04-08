package com.ignfab.minalac.generator.parameters.processors.post;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.processors.post.IdentityPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.processors.post.SequentialPostProcessor;
import com.ignfab.minalac.generator.processors.post.TestingPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class SequentialPostProcessorParamsTest {
    @Test
    @DisplayName("Empty sequential post-processor params is valid")
    public void testValidateEmpty() {
        SequentialPostProcessorParams empty = new SequentialPostProcessorParams(List.of());
        assertDoesNotThrow(empty::validate);
    }

    @Test
    @DisplayName("Sequential post-processor params with a single post-processor is valid")
    public void testValidateSingle() {
        SequentialPostProcessorParams single = new SequentialPostProcessorParams(List.of(
            TestingPostProcessorParams.VALID
        ));
        assertDoesNotThrow(single::validate);
    }

    @Test
    @DisplayName("Sequential post-processor params with multiple post-processors is valid")
    public void testValidateMultiple() {
        SequentialPostProcessorParams multiple = new SequentialPostProcessorParams(List.of(
            TestingPostProcessorParams.VALID,
            TestingPostProcessorParams.VALID
        ));
        assertDoesNotThrow(multiple::validate);
    }

    @Test
    @DisplayName("Sequential post-processor params with an invalid post-processor is invalid")
    public void testValidateInvalid() {
        SequentialPostProcessorParams invalid = new SequentialPostProcessorParams(List.of(
            TestingPostProcessorParams.VALID,
            TestingPostProcessorParams.INVALID
        ));
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    @DisplayName("Empty sequential post-processor params creates an identity post-processor")
    public void testCreateEmpty() {
        SequentialPostProcessorParams emptyParams = new SequentialPostProcessorParams(List.of());
        PostProcessor<?, ?> emptyPostProcessor = assertDoesNotThrow(emptyParams::create);
        assertSame(IdentityPostProcessor.INSTANCE, emptyPostProcessor);
    }

    @Test
    @DisplayName("Sequential post-processor params with a single post-processor creates that post-processor")
    public void testCreateSingle() {
        SequentialPostProcessorParams singleParams = new SequentialPostProcessorParams(List.of(
            TestingPostProcessorParams.VALID
        ));
        PostProcessor<?, ?> singlePostProcessor = assertDoesNotThrow(singleParams::create);
        assertInstanceOf(TestingPostProcessor.class, singlePostProcessor);
    }

    @Test
    @DisplayName("Sequential post-processor params with multiple post-processors creates a sequential post-processor")
    public void testCreateMultiple() {
        SequentialPostProcessorParams multipleParams = new SequentialPostProcessorParams(List.of(
            TestingPostProcessorParams.VALID,
            TestingPostProcessorParams.VALID
        ));
        PostProcessor<?, ?> multiplePostProcessor = assertDoesNotThrow(multipleParams::create);
        assertInstanceOf(SequentialPostProcessor.class, multiplePostProcessor);
    }
}
