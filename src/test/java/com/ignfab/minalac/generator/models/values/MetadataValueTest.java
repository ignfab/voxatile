package com.ignfab.minalac.generator.models.values;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.TestingModel;

import static com.ignfab.minalac.generator.models.values.ModelValueTester.*;

public class MetadataValueTest {
    @Test
    public void test() {
        assertModelValueAbsent(new MetadataValue("unknown"), new TestingModel());
        assertModelValueAbsent(new MetadataValue("string"), new TestingModel(Map.of("string", "this is not a number")));

        assertModelValue(new MetadataValue("integer"), 5, new TestingModel(Map.of("integer", 5)));
        assertModelValue(new MetadataValue("double"), 3.7, new TestingModel(Map.of("double", 3.7)));
    }
}
