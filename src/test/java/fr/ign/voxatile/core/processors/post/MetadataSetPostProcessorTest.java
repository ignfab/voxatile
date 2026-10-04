package fr.ign.voxatile.core.processors.post;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.TestingModel;
import fr.ign.voxatile.core.models.values.AbsentValue;
import fr.ign.voxatile.core.models.values.FixedValue;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataSetPostProcessorTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel(Map.of("a", 1d));
    }

    private TestingModel getProcessed(MetadataSetPostProcessor postProcessor) {
        Model processedModel = assertDoesNotThrow(() -> postProcessor.process(model));
        return assertInstanceOf(TestingModel.class, processedModel);
    }

    @Test
    public void testSimpleSet() {
        TestingModel processed = getProcessed(new MetadataSetPostProcessor("b", new FixedValue(2), false, false));
        processed.assertMetadata("b", 2d, "Metadata set");
    }

    @Test
    public void testSetAbsentAllowed() {
        TestingModel processed = getProcessed(new MetadataSetPostProcessor("a", AbsentValue.INSTANCE, false, false));
        processed.assertMetadataAbsent("a", "Metadata cleared");
    }

    @Test
    public void testSetAbsentForbidden() {
        TestingModel processed = getProcessed(new MetadataSetPostProcessor("a", AbsentValue.INSTANCE, true, false));
        processed.assertMetadata("a", 1d, "Metadata NOT cleared");
    }

    @Test
    public void testOverwriteAllowed() {
        TestingModel processed = getProcessed(new MetadataSetPostProcessor("a", new FixedValue(2), false, false));
        processed.assertMetadata("a", 2d, "Metadata overwritten");
    }

    @Test
    public void testOverwriteForbidden() {
        TestingModel processed = getProcessed(new MetadataSetPostProcessor("a", new FixedValue(2), false, true));
        processed.assertMetadata("a", 1d, "Metadata NOT overwritten");
    }
}
