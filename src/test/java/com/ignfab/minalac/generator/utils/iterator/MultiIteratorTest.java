package com.ignfab.minalac.generator.utils.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

public class MultiIteratorTest {
    @Test
    void testIterator() {
        LinkedList<Iterable<String>> list = new LinkedList<>();

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new MultiIterator<>(list)
        );

        list.add(Arrays.asList(new String[] { "Spirou", "Fantasio" }));

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Fantasio", "Spirou" }),
            new MultiIterator<>(list)
        );

        list.add(Arrays.asList(new String[] { "Riri", "Fifi", "Loulou" }));
        list.add(Arrays.asList(new String[] { "Croquignol", "Filochard", "Ribouldingue" }));

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Croquignol", "Fantasio", "Fifi", "Filochard", "Loulou",  "Riri", "Ribouldingue", "Spirou" }),
            new MultiIterator<>(list)
        );

        list.add(Collections.emptyList());

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Croquignol", "Fantasio", "Fifi", "Filochard", "Loulou",  "Riri", "Ribouldingue", "Spirou" }),
            new MultiIterator<>(list)
        );
    }

    @Test
    void testConcat() {

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            MultiIterator.concat()
        );

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Pile", "Face" }),
            MultiIterator.concat(
                Arrays.asList(new String[] { "Pile", "Face" })
            )
        );

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Trefle", "Careau", "Coeur", "Pique" }),
            MultiIterator.concat(
                Arrays.asList(new String[] { "Trefle", "Pique" }),
                Arrays.asList(new String[] { "Careau", "Coeur" })
            )
        );

        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(new String[] { "Pair", "Impair" }),
            MultiIterator.concat(
                Collections.emptyList(),
                Arrays.asList(new String[] { "Pair", "Impair" })
            )
        );
    }
}


