package fr.ign.voxatile.core.parameters.models.values;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.values.MetadataValue;
import fr.ign.voxatile.core.models.values.ModelValue;
import fr.ign.voxatile.core.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataValueParamsTest {
    @Test
    public void testDeserialize() {
        MetadataValueParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(MetadataValueParams.class, "metadata: height"));
        assertEquals("height", params.metadata);

        assertDoesNotThrow(params::validate);
        ModelValue value = assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
        assertInstanceOf(MetadataValue.class, value);

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(MetadataValueParams.class, "metadata:"));
    }

    @Test
    public void testValidate() {
        MetadataValueParams paramsValid = new MetadataValueParams("height");
        assertDoesNotThrow(paramsValid::validate);

        MetadataValueParams paramsInvalid = new MetadataValueParams("");
        assertThrows(IllegalArgumentException.class, paramsInvalid::validate);
    }
}
