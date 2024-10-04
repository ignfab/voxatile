package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataParsePostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of(
            "string", "value",
            "int", 7
        ));
    }

    @Test
    public void testCapabilities() {
        MetadataParsePostProcessor<?> postProcessor = new MetadataParsePostProcessor<>(
            "name",
            Object.class,
            Function.identity(),
            MetadataParsePostProcessor.ParsingFailurePolicy.IGNORE,
            false,
            false
        );
        assertTrue(postProcessor.acceptedModelType().isAssignableFrom(TestingModel.class), "post-processor accepts any model type");
        assertTrue(TestingModel.class.isAssignableFrom(postProcessor.processedModelType(TestingModel.class)), "post-processor produces same model type");
    }

    private TestingModel getProcessed(MetadataParsePostProcessor<?> postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    public void testSimpleParse() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "int",
            String.class,
            Objects::toString,
            // Failure policy is irrelevant for this test as there is no failure
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            false
        ));
        processed.assertMetadata("int", "7");
        processed.assertMetadata("string", "value", "Unrelated metadata untouched");
    }

    @Test
    public void testParsingError() {
        assertThrows(GenerationFailedException.class, () -> new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> {
                throw new RuntimeException();
            },
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            false
        ).process(model));
    }

    @Test
    public void testMissingAllowed() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "missing",
            String.class,
            Objects::toString,
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            false
        ));
        processed.assertMetadataAbsent("missing", "Metadata still absent");
    }

    @Test
    public void testMissingForbidden() {
        assertThrows(GenerationFailedException.class, () -> new MetadataParsePostProcessor<>(
            "missing",
            String.class,
            Objects::toString,
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            true,
            false
        ).process(model));
    }

    @Test
    public void testNullAllowed() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> null,
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            false
        ));
        processed.assertMetadataAbsent("int", "Metadata removed");
    }

    @Test
    public void testNullForbidden() {
        assertThrows(GenerationFailedException.class, () -> new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> null,
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            true
        ).process(model));
    }

    @Test
    public void testFailurePolicyIgnore() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> {
                throw new RuntimeException();
            },
            MetadataParsePostProcessor.ParsingFailurePolicy.IGNORE,
            false,
            false
        ));
        processed.assertMetadata("int", 7, "Original value kept");
    }

    @Test
    public void testFailurePolicyRemove() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> {
                throw new RuntimeException();
            },
            MetadataParsePostProcessor.ParsingFailurePolicy.REMOVE_METADATA,
            false,
            false
        ));
        processed.assertMetadataAbsent("int", "Original value cleared");
    }

    @Test
    public void testFailurePolicySkip() {
        assertThrows(IgnorableException.class, () -> new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> {
                throw new RuntimeException();
            },
            MetadataParsePostProcessor.ParsingFailurePolicy.SKIP_MODEL,
            false,
            false
        ).process(model));
    }

    @Test
    public void testFailurePolicyError() {
        assertThrows(GenerationFailedException.class, () -> new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> {
                throw new RuntimeException();
            },
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            false,
            false
        ).process(model));
    }
}
