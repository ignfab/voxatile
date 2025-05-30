package com.ignfab.minalac.generator.utils.iterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class IteratorsTest {
    @Test
    void testArray() {
        assertEmpty(Iterators.array(new String[0]));

        assertBrowsesAllOnce(List.of("Disco"),
            Iterators.iterator("Disco"));

        assertBrowsesAllOnce(List.of("Upside", "down", "round", "round"),
            Iterators.iterator("Upside", "down", "round", "round"));
    }

    @Test
    void testFilter() {
        assertBrowsesAllOnce(
            List.of(1, 3, 5, 7, 9),
            Iterators.filter(
                Iterators.iterator(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 != 0
            )
        );

        assertBrowsesAllOnce(
            List.of(0, 2, 4, 6, 8, 10),
            Iterators.filter(
                Iterators.iterator(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 == 0
            )
        );

        assertEmpty(
            Iterators.filter(
                Iterators.iterator("a", "b", "c"),
                item -> false
            )
        );

        assertEmpty(
            Iterators.filter(
                Iterators.iterator(),
                item -> true
            )
        );
    }

    @Test
    void testIterator() {
        assertEmpty(Iterators.iterator());

        assertBrowsesAllOnce(List.of("Here"),
            Iterators.iterator("Here"));

        assertBrowsesAllOnce(List.of("North", "South", "East", "West"),
            Iterators.iterator("North", "East", "South", "West"));
    }

    @Test
    void testSingleton() {
        assertBrowsesAllOnce(
            List.of("The One Ring"),
            Iterators.singleton("The One Ring")
        );

        assertBrowsesAllOnce(
            Collections.singletonList(null),
            Iterators.singleton(null)
        );
    }

    @Test
    void testRemap() {
        assertBrowsesAllOnce(
            List.of(3, 5, 5, 6, 6, 6, 7),
            Iterators.remap(
                List.of(
                    "Doc", "Grumpy", "Happy", "Sleepy", "Bashful", "Sneezy", "Dopey"
                ).iterator(),
                String::length
            )
        );

        assertEmpty(Iterators.remap(Collections.emptyIterator(), String::length));
    }

    @Test
    void testUnion() {
        assertBrowsesAllOnce(
            List.of("Heads", "Tails"),
            Iterators.union(
                List.of("Heads", "Tails").iterator()
            )
        );

        assertBrowsesAllOnce(
            List.of("Clover", "Diamond", "Heart", "Spade"),
            Iterators.union(
                List.of("Clover", "Spade").iterator(),
                List.of("Diamond", "Heart").iterator()
            )
        );

        assertBrowsesAllOnce(
            List.of("Odd", "Even"),
            Iterators.union(
                Collections.emptyIterator(),
                List.of("Odd", "Even").iterator()
            )
        );
    }

    @Test
    void testUnwrap() {
        List<Iterator<String>> list;

        list = new LinkedList<>();
        assertEmpty(Iterators.unwrap(list.iterator()));

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam", "Merry", "Pippin"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Sam", "Merry", "Pippin"),
            Iterators.unwrap(list.iterator())
        );

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam", "Merry", "Pippin"));
        list.add(Iterators.iterator("Gandalf"));
        list.add(Iterators.iterator("Boromir", "Aragorn"));
        list.add(Iterators.iterator("Gimli", "Legolas"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Gandalf", "Sam", "Merry", "Gimli", "Legolas", "Pippin", "Boromir", "Aragorn"),
            Iterators.unwrap(list.iterator())
        );

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam"));
        list.add(Iterators.iterator());
        list.add(Iterators.iterator("Gollum"));

        assertBrowsesAllOnce(
            List.of("Frodo", "Sam", "Gollum"),
            Iterators.unwrap(list.iterator())
        );
    }
}
