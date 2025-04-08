package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataDefaultPostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of("value", 7));
    }

    private TestingModel getProcessed(MetadataDefaultPostProcessor postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    public void testMetadataAbsent() {
        TestingModel processed = getProcessed(new MetadataDefaultPostProcessor("extra", 42));
        processed.assertMetadata("extra", 42);
    }

    @Test
    public void testMetadataPresent() {
        TestingModel processed = getProcessed(new MetadataDefaultPostProcessor("value", 42));
        processed.assertMetadata("value", 7);
    }
}
