package fr.ign.voxatile.core.parameters.models.filters;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGeneration;
import fr.ign.voxatile.core.models.filters.ModelFilterEmptyGeometry;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterEmptyGeometryParamsTest {
    @Test
    public void testCreate() {
        assertSame(ModelFilterEmptyGeometry.INSTANCE, new ModelFilterEmptyGeometryParams().create(TestingGeneration.UNUSED));
    }
}
