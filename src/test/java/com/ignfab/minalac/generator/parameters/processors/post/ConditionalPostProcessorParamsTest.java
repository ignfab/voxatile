package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.parameters.models.filters.TestingModelFilterParams;
import com.ignfab.minalac.generator.processors.post.ConditionalPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionalPostProcessorParamsTest {
    @Test
    @DisplayName("Simple conditional post-processor params is valid")
    public void testValidateValid() {
        ConditionalPostProcessorParams valid = new ConditionalPostProcessorParams(
            TestingModelFilterParams.VALID,
            TestingPostProcessorParams.VALID
        );
        assertDoesNotThrow(valid::validate);
    }

    @Test
    @DisplayName("Conditional post-processor params with an invalid model filter is invalid")
    public void testValidateInvalidModelFilter() {
        ConditionalPostProcessorParams invalid = new ConditionalPostProcessorParams(
            TestingModelFilterParams.INVALID,
            TestingPostProcessorParams.VALID
        );
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    @DisplayName("Conditional post-processor params with an invalid post-processor is invalid")
    public void testValidateInvalidPostProcessor() {
        ConditionalPostProcessorParams invalidIfTrue = new ConditionalPostProcessorParams(
            TestingModelFilterParams.VALID,
            TestingPostProcessorParams.INVALID
        );
        assertThrows(IllegalArgumentException.class, invalidIfTrue::validate);

        ConditionalPostProcessorParams invalidIfFalse = new ConditionalPostProcessorParams(
            TestingModelFilterParams.VALID,
            TestingPostProcessorParams.VALID
        );
        invalidIfFalse.postProcessorIfFalse = TestingPostProcessorParams.INVALID;
        assertThrows(IllegalArgumentException.class, invalidIfFalse::validate);
    }

    @Test
    @DisplayName("Conditional post-processor params creates the correct conditional post-processor")
    public void testCreate() {
        TestingModel matching = new TestingModel("matching");
        TestingModel notMatching = new TestingModel("not-matching");

        ConditionalPostProcessorParams params = new ConditionalPostProcessorParams(
            new TestingModelFilterParams(matching),
            new TestingPostProcessorParams("matched")
        );
        params.postProcessorIfFalse = new TestingPostProcessorParams("not-matched");
        @SuppressWarnings("unchecked") // TestingPostProcessor is generic
        PostProcessor<Model, ?> postProcessor = (PostProcessor<Model, ?>) assertDoesNotThrow(params::create);
        assertInstanceOf(ConditionalPostProcessor.class, postProcessor);

        assertDoesNotThrow(() -> {
            postProcessor.process(matching);
            postProcessor.process(notMatching);
        });
        matching.assertMetadataPresent("matched");
        matching.assertMetadataAbsent("not-matched");
        notMatching.assertMetadataPresent("not-matched");
        notMatching.assertMetadataAbsent("matched");
    }
}
