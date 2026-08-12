package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataValueMappingPostProcessorTest {

    @Test
    public void testProcess() {
        TestingModel result;

        Map<String, String> simpleCorrespondanceValue = Map.of("a", "b", "c", "d");
        PostProcessor<Model, Model> processorWithDefault = new MetadataValueMappingPostProcessor<>(
            String.class,
            "metadata",
            simpleCorrespondanceValue,
            "z",
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        );
        // Regular usage
        result = assertInstanceOf(
            TestingModel.class,
            assertDoesNotThrow(() -> processorWithDefault.process(new TestingModel(Map.of("metadata", "a", "metadata2", "b"))))
        );
        result.assertMetadata("metadata", "b");

        // Default
        result = assertInstanceOf(
            TestingModel.class,
            assertDoesNotThrow(() -> processorWithDefault.process(new TestingModel(Map.of("metadata", "e", "metadata2", "d"))))
        );
        result.assertMetadata("metadata", "z");

        PostProcessor<Model, Model> processorWithoutDefault = new MetadataValueMappingPostProcessor<>(
            String.class,
            "metadata",
            simpleCorrespondanceValue,
            null,
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        );

        // Metadata missing
        assertThrows(GenerationFailedException.class, () -> processorWithoutDefault.process(new TestingModel()));
        // No correspondance found
        assertThrows(GenerationFailedException.class, () -> processorWithoutDefault.process(new TestingModel(Map.of("metadata", "e", "metadata2", "d"))));

        // Testing policies
        PostProcessor<Model, Model> processorDiscardIgnorePolicy = new MetadataValueMappingPostProcessor<>(
            String.class,
            "metadata",
            simpleCorrespondanceValue,
            null,
            FailurePolicy.DISCARD_MODEL,
            FailurePolicy.IGNORE
        );

        assertThrows(IgnorableException.class, () -> processorDiscardIgnorePolicy.process(new TestingModel()));
        result = assertInstanceOf(
            TestingModel.class,
            assertDoesNotThrow(() -> processorDiscardIgnorePolicy.process(new TestingModel(Map.of("metadata", "e", "metadata2", "d"))))
        );
        result.assertMetadata("metadata", "e");

        PostProcessor<Model, Model> processorIgnoreDiscardPolicy = new MetadataValueMappingPostProcessor<>(
            String.class,
            "metadata",
            simpleCorrespondanceValue,
            null,
            FailurePolicy.IGNORE,
            FailurePolicy.DISCARD_MODEL
        );

        assertDoesNotThrow(() -> processorIgnoreDiscardPolicy.process(new TestingModel()));
        assertThrows(IgnorableException.class, () -> processorIgnoreDiscardPolicy.process(new TestingModel(Map.of("metadata", "e", "metadata2", "d"))));

        PostProcessor<Model, Model> processorRemoveMetadataPolicy = new MetadataValueMappingPostProcessor<>(
            String.class,
            "metadata",
            simpleCorrespondanceValue,
            null,
            FailurePolicy.REMOVE_METADATA,
            FailurePolicy.REMOVE_METADATA
        );

        assertDoesNotThrow(() -> processorRemoveMetadataPolicy.process(new TestingModel()));
        result = assertInstanceOf(
            TestingModel.class,
            assertDoesNotThrow(() -> processorRemoveMetadataPolicy.process(new TestingModel(Map.of("metadata", "e", "metadata2", "d"))))
        );
        result.assertMetadataAbsent("metadata");
    }
}
