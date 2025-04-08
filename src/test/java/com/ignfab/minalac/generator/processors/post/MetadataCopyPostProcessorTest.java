package com.ignfab.minalac.generator.processors.post;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataCopyPostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of(
            "a", 1,
            "b", 2,
            "c", 3
        ));
    }

    private TestingModel getProcessed(MetadataCopyPostProcessor postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    public void testSimpleCopy() {
        TestingModel processed = getProcessed(new MetadataCopyPostProcessor("a", "d", false, false));
        processed.assertMetadata("a", 1, "Original metadata untouched");
        processed.assertMetadata("b", 2, "Unrelated metadata untouched");
        processed.assertMetadata("d", 1, "Destination metadata copied");
    }

    @Test
    public void testAbsentCopyAllowed() {
        TestingModel processed = getProcessed(new MetadataCopyPostProcessor("d", "c", false, false));
        processed.assertMetadataAbsent("d", "Original metadata still absent");
        processed.assertMetadataAbsent("c", "Destination metadata cleared");
    }

    @Test
    public void testAbsentCopyForbidden() {
        TestingModel processed = getProcessed(new MetadataCopyPostProcessor("d", "c", true, false));
        processed.assertMetadataAbsent("d", "Original metadata still absent");
        processed.assertMetadata("c", 3, "Destination metadata NOT cleared");
    }

    @Test
    public void testOverwriteCopyAllowed() {
        TestingModel processed = getProcessed(new MetadataCopyPostProcessor("a", "c", false, false));
        processed.assertMetadata("a", 1, "Original metadata untouched");
        processed.assertMetadata("c", 1, "Destination metadata overwritten");
    }

    @Test
    public void testOverwriteCopyForbidden() {
        TestingModel processed = getProcessed(new MetadataCopyPostProcessor("a", "c", false, true));
        processed.assertMetadata("a", 1, "Original metadata untouched");
        processed.assertMetadata("c", 3, "Destination metadata NOT overwritten");
    }
}
