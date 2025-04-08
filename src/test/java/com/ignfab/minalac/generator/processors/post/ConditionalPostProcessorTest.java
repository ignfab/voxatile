package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.models.filters.TestingModelFilter;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionalPostProcessorTest {
    private TestingModel modelA;
    private TestingModel modelB;
    private TestingModel modelC;

    @BeforeEach
    public void setUp() {
        modelA = new TestingModel("A", Map.of("value", 7));
        modelB = new TestingModel("B", Map.of("value", "test"));
        modelC = new TestingModel("C");
    }

    private void processAll(ConditionalPostProcessor<? super TestingModel, ?> postProcessor) {
        Model processedModelA = assertDoesNotThrow(() -> postProcessor.process(modelA));
        Model processedModelB = assertDoesNotThrow(() -> postProcessor.process(modelB));
        Model processedModelC = assertDoesNotThrow(() -> postProcessor.process(modelC));
        modelA = processedModelA == null ? null : assertInstanceOf(TestingModel.class, processedModelA);
        modelB = processedModelB == null ? null : assertInstanceOf(TestingModel.class, processedModelB);
        modelC = processedModelC == null ? null : assertInstanceOf(TestingModel.class, processedModelC);
    }

    @Test
    @DisplayName("Conditional post-processor processes models using the correct underlying post-processor")
    public void testProcess() {
        TestingPostProcessor testingPostProcessor1 = new TestingPostProcessor();
        TestingPostProcessor testingPostProcessor2 = new TestingPostProcessor();
        processAll(new ConditionalPostProcessor<>(new TestingModelFilter(modelB), testingPostProcessor1, testingPostProcessor2));

        testingPostProcessor1.assertNotPostProcessed(modelA);
        testingPostProcessor1.assertPostProcessed(modelB);
        testingPostProcessor1.assertNotPostProcessed(modelC);

        testingPostProcessor2.assertPostProcessed(modelA);
        testingPostProcessor2.assertNotPostProcessed(modelB);
        testingPostProcessor2.assertPostProcessed(modelC);
    }

    @Test
    @DisplayName("Conditional post-processor returns the model processed by its underlying post-processors")
    public void testReturnValue() {
        TestingModel a = modelA;
        TestingModel b = modelB;
        processAll(new ConditionalPostProcessor<>(new TestingModelFilter(modelC), DiscardPostProcessor.INSTANCE, IdentityPostProcessor.INSTANCE));
        assertSame(a, modelA);
        assertSame(b, modelB);
        assertNull(modelC);
    }

    @Test
    @DisplayName("Conditional post-processor respects the processing type of its underlying post-processors")
    public void testCheckProcessingType() {
        ConditionalPostProcessor<?, ?> postProcessor = new ConditionalPostProcessor<>(
            new TestingModelFilter(modelA),
            new PostProcessorTest.ComplexPostProcessor(),
            IdentityPostProcessor.INSTANCE
        );
        assertThrows(IllegalArgumentException.class, () -> postProcessor.checkProcessingType(Model.class));
        Class<?> processedModelType = assertDoesNotThrow(() -> postProcessor.checkProcessingType(TestingModel.class));
        assertEquals(TestingModel.class, processedModelType);
    }
}
