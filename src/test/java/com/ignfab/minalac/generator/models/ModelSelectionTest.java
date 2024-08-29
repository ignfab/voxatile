package com.ignfab.minalac.generator.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.assertBrowsesAllOnce;
import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.assertEmpty;

public class ModelSelectionTest {

    @Test
    public void testIterator() {
        ModelStore store = new ModelStore();

        Model modelA = new ModelImpl();
        Model modelB = new ModelImpl();
        Model model5 = new ModelImpl();
        Model model2 = new ModelImpl();

        store.add("letter", modelA);
        store.add("digit", model5);
        store.add("letter", modelB);
        store.add("letter", modelA);
        store.add("digit", model2);

        ModelSelection selection = new ModelSelection(store, "letter");
        Iterator<Model> iterator = selection.iterator();

        assertBrowsesAllOnce(Arrays.asList(modelA, modelA, modelB), iterator);

        assertEmpty(new ModelSelection(store, "special"));
    }

    private static class ModelImpl extends Model {
        ModelImpl() {}

        @Override
        public String salt() {
            throw new UnsupportedOperationException("Unimplemented method 'salt'");
        }
    }
}
