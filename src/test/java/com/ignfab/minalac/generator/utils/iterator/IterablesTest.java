package com.ignfab.minalac.generator.utils.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class IterablesTest {

    @Test
    void testArray() {
        assertEmpty(Iterables.array(new String[0]));

        assertBrowsesAllOnce(Arrays.asList("Disco"),
            Iterables.iterable("Disco"));

        assertBrowsesAllOnce(Arrays.asList("Upside", "down", "round", "round"),
            Iterables.iterable("Upside", "down", "round", "round"));
    }

    @Test
    void testFilter() {
        assertBrowsesAllOnce(
            Arrays.asList(1, 3, 5, 7, 9),
            Iterables.filter(
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 != 0
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList(0, 2, 4, 6, 8, 10),
            Iterables.filter(
                Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 == 0
            )
        );

        assertEmpty(
            Iterables.filter(
                Arrays.asList("a", "b", "c"),
                _ -> false
            )
        );

        assertEmpty(
            Iterables.filter(
                Collections.emptyList(),
                _ -> true
            )
        );
    }

    @Test
    void testSingleton() {
        assertBrowsesAllOnce(
            Arrays.asList("The One Ring"),
            Iterables.singleton("The One Ring")
        );

        assertBrowsesAllOnce(
            Arrays.asList((Object) null),
            Iterables.singleton(null)
        );
    }

    @Test
    void testRemap() {
        assertBrowsesAllOnce(
            Arrays.asList(3, 5, 5, 6, 6, 6, 7),
            Iterables.remap(
                Arrays.asList("Doc", "Grumpy", "Happy", "Sleepy", "Bashful", "Sneezy", "Dopey"),
                String::length
            )
        );

        assertBrowsesAllOnce(
            Collections.emptyList(),
            Iterables.remap(Collections.emptyList(), String::length)
        );
    }

    @Test
    void testUnion() {
        assertBrowsesAllOnce(
            Arrays.asList("Heads", "Tails"),
            Iterables.union(
                Arrays.asList("Heads", "Tails")
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList("Clover", "Diamond", "Heart", "Spade"),
            Iterables.union(
                Arrays.asList("Heart", "Diamond"),
                Arrays.asList("Clover", "Spade")
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList("Odd", "Even"),
            Iterables.union(
                Collections.emptyList(),
                Arrays.asList("Odd", "Even")
            )
        );
    }

    @Test
    void testUnwrap() {
        List<Iterable<String>> list;

        list = new LinkedList<>();
        assertBrowsesAllOnce(
            Collections.emptyList(),
            Iterables.unwrap(list)
        );

        list = new LinkedList<>();
        list.add(Arrays.asList("Frodo", "Sam", "Merry", "Pippin"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Sam", "Merry", "Pippin"),
            Iterables.unwrap(list)
        );

        list = new LinkedList<>();
        list.add(Arrays.asList("Frodo", "Sam", "Merry", "Pippin"));
        list.add(Arrays.asList("Gandalf"));
        list.add(Arrays.asList("Boromir", "Aragorn"));
        list.add(Arrays.asList("Gimli", "Legolas"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Gandalf", "Sam", "Merry", "Gimli", "Legolas", "Pippin", "Boromir", "Aragorn"),
            Iterables.unwrap(list)
        );

        list = new LinkedList<>();
        list.add(Arrays.asList("Frodo", "Sam"));
        list.add(Collections::emptyIterator);
        list.add(Arrays.asList("Gollum"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Sam", "Gollum"),
            Iterables.unwrap(list)
        );
    }

    @Test
    void testFlatMap() {
        assertBrowsesAllOnce(
            Arrays.asList(
                "11",
                "21", "22",
                "31", "32", "33"
            ),
            Iterables.flatMap(Arrays.asList(0, 1, 2, 3), n -> {
                List<String> list = new ArrayList<>();
                for (int i = 1; i <= n; i++)
                    list.add(n + "" + i);
                return list;
            })
        );
    }
}
