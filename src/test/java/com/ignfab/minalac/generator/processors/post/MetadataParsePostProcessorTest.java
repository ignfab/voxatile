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

public class MetadataParsePostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of(
            "string", "value",
            "int", 7
        ));
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
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR
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
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR
        ).process(model));
    }

    @Test
    public void testMissingAllowed() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "missing",
            String.class,
            Objects::toString,
            MetadataParsePostProcessor.ParsingFailurePolicy.IGNORE,
            MetadataParsePostProcessor.ParsingFailurePolicy.IGNORE
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
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR
        ).process(model));
    }

    @Test
    public void testNullAllowed() {
        TestingModel processed = getProcessed(new MetadataParsePostProcessor<>(
            "int",
            String.class,
            obj -> null,
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR,
            MetadataParsePostProcessor.ParsingFailurePolicy.REMOVE_METADATA
        ));
        processed.assertMetadataAbsent("int", "Metadata removed");
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
            MetadataParsePostProcessor.ParsingFailurePolicy.IGNORE
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
            MetadataParsePostProcessor.ParsingFailurePolicy.REMOVE_METADATA
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
            MetadataParsePostProcessor.ParsingFailurePolicy.DISCARD_MODEL,
            MetadataParsePostProcessor.ParsingFailurePolicy.DISCARD_MODEL
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
            MetadataParsePostProcessor.ParsingFailurePolicy.ERROR
        ).process(model));
    }
}
