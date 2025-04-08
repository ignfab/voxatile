package com.ignfab.minalac.generator.processors.post;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class SequentialPostProcessorTest {
    private TestingModel model;
    private TestingPostProcessor ppA;
    private TestingPostProcessor ppB;
    private TestingPostProcessor ppC;

    @BeforeEach
    public void setUp() {
        model = new TestingModel();
        ppA = new TestingPostProcessor("A");
        ppB = new TestingPostProcessor("B");
        ppC = new TestingPostProcessor("C");
    }

    private TestingModel getProcessed(SequentialPostProcessor<? super TestingModel, ?> postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return processedModel == null ? null : assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    @DisplayName("Sequential post-processor applies its underlying post-processors")
    public void testSimple() {
        TestingModel processed = getProcessed(new SequentialPostProcessor<>(List.of(ppA, ppB, ppC)));
        assertSame(model, processed);
        ppA.assertPostProcessed(model);
        ppB.assertPostProcessed(model);
        ppC.assertPostProcessed(model);
    }

    @Test
    @DisplayName("Sequential post-processor stops when model is discarded by one of its underlying post-processors")
    public void testDiscarded() {
        TestingModel discarded = getProcessed(new SequentialPostProcessor<>(List.of(ppA, ppB, DiscardPostProcessor.INSTANCE, ppC)));
        assertNull(discarded);
        ppA.assertPostProcessed(model);
        ppB.assertPostProcessed(model);
        ppC.assertNotPostProcessed(model);
    }

    @Test
    @DisplayName("Sequential post-processor checks the processing types of its underlying post-processors")
    public void testCheckProcessingType() {
        SequentialPostProcessor<?, ?> postProcessor = new SequentialPostProcessor<>(List.of(
            new TestingPostProcessor(),
            new PostProcessorTest.ComplexPostProcessor(),
            new PostProcessorTest.SimplePostProcessor(),
            new TestingPostProcessor()
        ));
        assertThrows(IllegalArgumentException.class, () -> postProcessor.checkProcessingType(Model.class));
        Class<?> processedModelType = assertDoesNotThrow(() -> postProcessor.checkProcessingType(TestingModel.class));
        assertEquals(TestingModel.Subclass.class, processedModelType);
    }
}
