package fr.ign.voxatile.core.models.values;

import org.junit.jupiter.api.Test;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;

public class AbsentValueTest {
    @Test
    public void test() {
        assertModelValueAbsent(AbsentValue.INSTANCE);
    }
}
