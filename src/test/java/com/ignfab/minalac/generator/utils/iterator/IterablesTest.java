package com.ignfab.minalac.generator.utils.iterator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class IterablesTest {

    @Test
    void testArray() {
        assertEmpty(Iterables.array(new String[0]));

        assertBrowsesAllOnce(List.of("Disco"),
            Iterables.iterable("Disco"));

        assertBrowsesAllOnce(List.of("Upside", "down", "round", "round"),
            Iterables.iterable("Upside", "down", "round", "round"));
    }

    @Test
    void testFilter() {
        assertBrowsesAllOnce(
            List.of(1, 3, 5, 7, 9),
            Iterables.filter(
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 != 0
            )
        );

        assertBrowsesAllOnce(
            List.of(0, 2, 4, 6, 8, 10),
            Iterables.filter(
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 == 0
            )
        );

        assertEmpty(
            Iterables.filter(
                List.of("a", "b", "c"),
                item -> false
            )
        );

        assertEmpty(
            Iterables.filter(
                Collections.emptyList(),
                item -> true
            )
        );
    }

    @Test
    void testSingleton() {
        assertBrowsesAllOnce(
            List.of("The One Ring"),
            Iterables.singleton("The One Ring")
        );

        assertBrowsesAllOnce(
            Collections.singletonList(null),
            Iterables.singleton(null)
        );
    }

    @Test
    void testRemap() {
        assertBrowsesAllOnce(
            List.of(3, 5, 5, 6, 6, 6, 7),
            Iterables.remap(
                List.of("Doc", "Grumpy", "Happy", "Sleepy", "Bashful", "Sneezy", "Dopey"),
                String::length
            )
        );

        assertEmpty(Iterables.remap(Collections.emptyList(), String::length));
    }

    @Test
    void testUnion() {
        assertBrowsesAllOnce(
            List.of("Heads", "Tails"),
            Iterables.union(
                List.of("Heads", "Tails")
            )
        );

        assertBrowsesAllOnce(
            List.of("Clover", "Diamond", "Heart", "Spade"),
            Iterables.union(
                List.of("Heart", "Diamond"),
                List.of("Clover", "Spade")
            )
        );

        assertBrowsesAllOnce(
            List.of("Odd", "Even"),
            Iterables.union(
                Collections.emptyList(),
                List.of("Odd", "Even")
            )
        );
    }

    @Test
    void testUnwrap() {
        List<Iterable<String>> list;

        list = new LinkedList<>();
        assertEmpty(Iterables.unwrap(list));

        list = new LinkedList<>();
        list.add(List.of("Frodo", "Sam", "Merry", "Pippin"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Sam", "Merry", "Pippin"),
            Iterables.unwrap(list)
        );

        list = new LinkedList<>();
        list.add(List.of("Frodo", "Sam", "Merry", "Pippin"));
        list.add(List.of("Gandalf"));
        list.add(List.of("Boromir", "Aragorn"));
        list.add(List.of("Gimli", "Legolas"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Gandalf", "Sam", "Merry", "Gimli", "Legolas", "Pippin", "Boromir", "Aragorn"),
            Iterables.unwrap(list)
        );

        list = new LinkedList<>();
        list.add(List.of("Frodo", "Sam"));
        list.add(Collections::emptyIterator);
        list.add(List.of("Gollum"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Sam", "Gollum"),
            Iterables.unwrap(list)
        );
    }
}
