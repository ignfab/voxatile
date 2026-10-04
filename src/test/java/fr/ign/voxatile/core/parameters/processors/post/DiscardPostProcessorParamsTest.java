package fr.ign.voxatile.core.parameters.processors.post;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.processors.post.DiscardPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPostProcessorParamsTest {
    @Test
    public void testCreate() {
        assertSame(DiscardPostProcessor.INSTANCE, new DiscardPostProcessorParams().create(TestingGeneration.UNUSED));
    }
}
