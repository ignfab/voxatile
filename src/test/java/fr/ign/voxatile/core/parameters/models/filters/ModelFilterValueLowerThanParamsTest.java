package fr.ign.voxatile.core.parameters.models.filters;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.filters.ModelFilterOnValue;
import fr.ign.voxatile.core.parameters.models.values.MetadataValueParams;
import fr.ign.voxatile.core.parameters.models.values.TestingModelValueParams;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterValueLowerThanParamsTest {
    @Test
    public void testValidate() {
        ModelFilterParams valid = new ModelFilterValueLowerThanParams(TestingModelValueParams.VALID, 1.);
        assertDoesNotThrow(valid::validate);

        ModelFilterParams invalid = new ModelFilterValueLowerThanParams(TestingModelValueParams.INVALID, 1.);
        assertThrows(IllegalArgumentException.class, invalid::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterParams params = new ModelFilterValueLowerThanParams(new MetadataValueParams("height"), 1.);
        assertInstanceOf(ModelFilterOnValue.class, assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED)));
    }
}
