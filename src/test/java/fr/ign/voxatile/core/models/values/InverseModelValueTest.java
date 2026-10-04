package fr.ign.voxatile.core.models.values;

import org.junit.jupiter.api.Test;

import static fr.ign.voxatile.core.models.values.ModelValueTester.*;
import static fr.ign.voxatile.core.models.values.ModelValueTester.assertModelValueAbsent;

public class InverseModelValueTest {
    @Test
    public void test() {
        assertModelValue(new InverseModelValue(new FixedValue(2)), 0.5);
        assertModelValueAbsent(new InverseModelValue(AbsentValue.INSTANCE));
        assertModelValueAbsent(new InverseModelValue(new FixedValue(0)));
    }
}
