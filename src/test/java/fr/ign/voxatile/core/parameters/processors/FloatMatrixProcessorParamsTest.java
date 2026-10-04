package fr.ign.voxatile.core.parameters.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class FloatMatrixProcessorParamsTest {
    @Test
    public void testCreate() throws FactoryException {
        Generation generation = new TestingGeneration(CRS.decode("EPSG:2154"));

        // A simple OK test
        final FloatMatrixProcessorParams params = new FloatMatrixProcessorParams();
        assertDoesNotThrow(() -> params.create(generation));
    }
}
