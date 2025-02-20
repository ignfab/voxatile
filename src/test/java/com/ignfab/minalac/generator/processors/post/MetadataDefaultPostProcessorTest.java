package com.ignfab.minalac.generator.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataDefaultPostProcessorTest {
    @Test
    public static void testConstructor() {
        assertDoesNotThrow(() -> new MetadataDefaultPostProcessor("toto", 1));
    }

    @Test
    public void testProcess() {
        MetadataDefaultPostProcessor processor = new MetadataDefaultPostProcessor("toto", 1);
        TestingModel model = new TestingModel();

        assertDoesNotThrow(() -> processor.process(model));
        model.assertMetadata("toto", 1);

        model.setMetadata("toto", 10);
        assertDoesNotThrow(() -> processor.process(model));
        model.assertMetadata("toto", 10);
    }
}
