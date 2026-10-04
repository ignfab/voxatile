package fr.ign.voxatile.core.models.values;

import org.junit.jupiter.api.Test;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;

public class UnaryOperationModelValueTest {
    @Test
    public void test() {
        assertModelValue(new UnaryOperationModelValue(new FixedValue(3), x -> x * 2), 6);
        assertModelValueAbsent(new UnaryOperationModelValue(AbsentValue.INSTANCE, x -> x * 2));
    }
}
