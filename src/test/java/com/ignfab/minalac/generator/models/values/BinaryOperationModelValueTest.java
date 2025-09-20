package com.ignfab.minalac.generator.models.values;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;

public class BinaryOperationModelValueTest {
    @Test
    public void test() {
        assertModelValue(new BinaryOperationModelValue(new FixedValue(2), new FixedValue(3), (a, b) -> a * b), 6);

        assertModelValueAbsent(new BinaryOperationModelValue(AbsentValue.INSTANCE, new FixedValue(3), (a, b) -> a * b));
        assertModelValueAbsent(new BinaryOperationModelValue(new FixedValue(2), AbsentValue.INSTANCE, (a, b) -> a * b));
        assertModelValueAbsent(new BinaryOperationModelValue(AbsentValue.INSTANCE, AbsentValue.INSTANCE, (a, b) -> a * b));
    }
}
