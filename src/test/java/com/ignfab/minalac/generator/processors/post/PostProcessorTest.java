package com.ignfab.minalac.generator.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class PostProcessorTest {
    @Test
    public void testGenericCapabilities() {
        TestingPostProcessor postProcessor = new TestingPostProcessor();
        Class<? extends Model> processedModelType = assertDoesNotThrow(() -> postProcessor.checkProcessingType(TestingModel.class), "post-processor accepts any model type");
        assertTrue(TestingModel.class.isAssignableFrom(processedModelType), "post-processor produces same model type");
    }

    @Test
    public void testSimpleCapabilities() {
        SimplePostProcessor postProcessor = new SimplePostProcessor();
        Class<? extends TestingModel> processedModelType = assertDoesNotThrow(() -> postProcessor.checkProcessingType(TestingModel.Subclass.class), "post-processor accepts given model type");
        assertTrue(TestingModel.Subclass.class.isAssignableFrom(processedModelType), "post-processor produces same model type");

        assertThrows(IllegalArgumentException.class, () -> postProcessor.checkProcessingType(Model.class), "post-processor rejects other model types");
    }

    public static final class SimplePostProcessor extends PostProcessor.Simple<TestingModel> {
        SimplePostProcessor() {
            super(TestingModel.class);
        }

        @Override
        public TestingModel process(TestingModel model) {
            return null;
        }
    }

    public static final class ComplexPostProcessor implements PostProcessor<TestingModel, TestingModel.Subclass> {
        @Override
        public Class<? extends TestingModel.Subclass> checkProcessingType(Class<? extends Model> inputModelType) throws IllegalArgumentException {
            if (!TestingModel.class.isAssignableFrom(inputModelType))
                throw new IllegalArgumentException();
            return TestingModel.Subclass.class;
        }

        @Override
        public TestingModel.Subclass process(TestingModel model) {
            return null;
        }
    }
}
