package fr.ign.voxatile.core.parameters.models.filters;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.filters.ModelFilterOnMetadataValue;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterMetadataEqualsParamsTest {
    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterMetadataEqualsParams("a", 1);
        assertDoesNotThrow(valid::validate);

        ModelFilterParams invalid = new ModelFilterMetadataEqualsParams("", 1);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterMetadataEqualsParams("a", 1);
        assertInstanceOf(ModelFilterOnMetadataValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}
