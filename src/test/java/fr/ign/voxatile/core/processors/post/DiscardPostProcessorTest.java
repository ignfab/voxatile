package fr.ign.voxatile.core.processors.post;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardPostProcessorTest {
    @Test
    public void testProcess() {
        Model model = new TestingModel();
        assertNull(DiscardPostProcessor.INSTANCE.process(model));
    }
}
