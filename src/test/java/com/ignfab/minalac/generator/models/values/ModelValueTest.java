package com.ignfab.minalac.generator.models.values;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.TestingModel;

import static org.junit.jupiter.api.Assertions.*;

public class ModelValueTest {
    private TestingModel model;

    @BeforeEach
    public void setUp() {
        model = new TestingModel();
    }

    @Test
    public void testGetAsInt() {
        Optional<Integer> fixedInt = new FixedValue(2.2).getAsInt(model);
        assertTrue(fixedInt.isPresent());
        assertEquals(2, fixedInt.get());

        Optional<Integer> absentInt = AbsentValue.INSTANCE.getAsInt(model);
        assertTrue(absentInt.isEmpty());
    }

    @Test
    public void testApplyAsDouble() {
        double fixed = new FixedValue(2.2).applyAsDouble(model);
        assertEquals(2.2, fixed);

        assertThrows(NoSuchElementException.class, () -> AbsentValue.INSTANCE.applyAsDouble(model));
    }
}
