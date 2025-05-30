package com.ignfab.minalac.generator.processors.post;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataFunctionPostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of(
            "string", "value",
            "int", 7
        ));
    }

    private TestingModel getProcessed(MetadataFunctionPostProcessor<?> postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    public void testSimpleParse() {
        TestingModel processed = getProcessed(new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            Objects::toString,
            // Failure policy is irrelevant for this test as there is no failure
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        ));
        processed.assertMetadata("int", "7");
        processed.assertMetadata("string", "value", "Unrelated metadata untouched");
    }

    @Test
    public void testParsingError() {
        assertThrows(GenerationFailedException.class, () -> new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> {
                throw new RuntimeException();
            },
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        ).process(model));
    }

    @Test
    public void testMissingAllowed() {
        TestingModel processed = getProcessed(new MetadataFunctionPostProcessor<>(
            String.class,
            "missing",
            Objects::toString,
            FailurePolicy.IGNORE,
            FailurePolicy.IGNORE
        ));
        processed.assertMetadataAbsent("missing", "Metadata still absent");
    }

    @Test
    public void testMissingForbidden() {
        assertThrows(GenerationFailedException.class, () -> new MetadataFunctionPostProcessor<>(
            String.class,
            "missing",
            Objects::toString,
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        ).process(model));
    }

    @Test
    public void testNullAllowed() {
        TestingModel processed = getProcessed(new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> null,
            FailurePolicy.ERROR,
            FailurePolicy.REMOVE_METADATA
        ));
        processed.assertMetadataAbsent("int", "Metadata removed");
    }

    @Test
    public void testFailurePolicyIgnore() {
        TestingModel processed = getProcessed(new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> {
                throw new RuntimeException();
            },
            FailurePolicy.IGNORE,
            FailurePolicy.IGNORE
        ));
        processed.assertMetadata("int", 7, "Original value kept");
    }

    @Test
    public void testFailurePolicyRemove() {
        TestingModel processed = getProcessed(new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> {
                throw new RuntimeException();
            },
            FailurePolicy.REMOVE_METADATA,
            FailurePolicy.REMOVE_METADATA
        ));
        processed.assertMetadataAbsent("int", "Original value cleared");
    }

    @Test
    public void testFailurePolicySkip() {
        assertThrows(IgnorableException.class, () -> new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> {
                throw new RuntimeException();
            },
            FailurePolicy.DISCARD_MODEL,
            FailurePolicy.DISCARD_MODEL
        ).process(model));
    }

    @Test
    public void testFailurePolicyError() {
        assertThrows(GenerationFailedException.class, () -> new MetadataFunctionPostProcessor<>(
            String.class,
            "int",
            _ -> {
                throw new RuntimeException();
            },
            FailurePolicy.ERROR,
            FailurePolicy.ERROR
        ).process(model));
    }
}
