package com.ignfab.minalac.generator.models;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class ModelSelectionTest {

    @Test
    public void testIterator() {
        ModelStore store = new ModelStore();

        Model modelA = new TestingModel("A", Map.of("a", 1));
        Model modelB = new TestingModel("B", Map.of("b", 2));
        Model modelC = new TestingModel("C");

        store.add("X", modelA);
        store.add("Y", modelA);
        store.add("X", modelB);
        store.add("X", modelA);
        store.add("Y", modelC);

        assertBrowsesAllOnce(Arrays.asList(modelA, modelA, modelB), new ModelSelection(store, "X", null).iterator());

        assertBrowsesAllOnce(Arrays.asList(modelA, modelA), new ModelSelection(store, "X", new ModelFilterHasMetadata("a")).iterator());

        assertBrowsesAllOnce(Arrays.asList(modelA, modelC), new ModelSelection(store, "Y", null).iterator());

        assertEmpty(new ModelSelection(store, "Y", new ModelFilterHasMetadata("b")));

        assertEmpty(new ModelSelection(store, "Z", null));
    }
}
