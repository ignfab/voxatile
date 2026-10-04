package fr.ign.voxatile.core.models.values;

import org.junit.jupiter.api.Test;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;

public class FixedValueTest {
    @Test
    public void test() {
        assertModelValue(new FixedValue(0), 0);
        assertModelValue(new FixedValue(2.5), 2.5);
        assertModelValue(new FixedValue(-7.3), -7.3);
    }
}
