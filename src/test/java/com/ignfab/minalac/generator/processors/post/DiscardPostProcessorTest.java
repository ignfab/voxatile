package com.ignfab.minalac.generator.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPostProcessorTest {
    @Test
    public void testProcess() {
        Model model = new TestingModel();
        assertNull(DiscardPostProcessor.INSTANCE.process(model));
    }
}
