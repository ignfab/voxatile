package com.ignfab.minalac.generator.utils.iterator;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IteratorsTest {
    @Test
    void testArray() {
        assertEmpty(Iterators.array(new String[0]));

        assertBrowsesAllOnce(Arrays.asList("Disco"),
            Iterators.iterator("Disco"));

        assertBrowsesAllOnce(Arrays.asList("Upside", "down", "round", "round"),
            Iterators.iterator("Upside", "down", "round", "round"));
    }

    @Test
    void testFilter() {
        assertBrowsesAllOnce(
            Arrays.asList(1, 3, 5, 7, 9),
            Iterators.filter(
                Iterators.iterator(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                item -> item % 2 != 0
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList(0, 2, 4, 6, 8, 10),
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

        assertBrowsesAllOnce(Arrays.asList("Here"),
            Iterators.iterator("Here"));

        assertBrowsesAllOnce(Arrays.asList("North", "South", "East", "West"),
            Iterators.iterator("North", "East", "South", "West"));
    }

    @Test
    void testSingleton() {
        assertBrowsesAllOnce(
            Arrays.asList("The One Ring"),
            Iterators.singleton("The One Ring")
        );

        assertBrowsesAllOnce(
            Arrays.asList((Object) null),
            Iterators.singleton(null)
        );
    }

    @Test
    void testRemap() {
        assertBrowsesAllOnce(
            Arrays.asList(3, 5, 5, 6, 6, 6, 7),
            Iterators.remap(
                Arrays.asList(
                    "Doc", "Grumpy", "Happy", "Sleepy", "Bashful", "Sneezy", "Dopey"
                ).iterator(),
                String::length
            )
        );

        assertBrowsesAllOnce(
            Collections.emptyList(),
            Iterators.remap(Collections.emptyIterator(), String::length)
        );
    }

    @Test
    void testUnion() {
        assertBrowsesAllOnce(
            Arrays.asList("Heads", "Tails"),
            Iterators.union(
                Arrays.asList("Heads", "Tails").iterator()
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList("Clover", "Diamond", "Heart", "Spade"),
            Iterators.union(
                Arrays.asList("Clover", "Spade").iterator(),
                Arrays.asList("Diamond", "Heart").iterator()
            )
        );

        assertBrowsesAllOnce(
            Arrays.asList("Odd", "Even"),
            Iterators.union(
                Collections.emptyIterator(),
                Arrays.asList("Odd", "Even").iterator()
            )
        );
    }

    @Test
    void testUnwrap() {
        List<Iterator<String>> list = new LinkedList<>();

        assertBrowsesAllOnce(
            Collections.emptyList(),
            Iterators.unwrap(list.iterator())
        );

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam", "Merry", "Pippin"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Sam", "Merry", "Pippin"),
            Iterators.unwrap(list.iterator())
        );

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam", "Merry", "Pippin"));
        list.add(Iterators.iterator("Gandalf"));
        list.add(Iterators.iterator("Boromir", "Aragorn"));
        list.add(Iterators.iterator("Gimli", "Legolas"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Gandalf", "Sam", "Merry", "Gimli", "Legolas", "Pippin", "Boromir", "Aragorn"),
            Iterators.unwrap(list.iterator())
        );

        list = new LinkedList<>();
        list.add(Iterators.iterator("Frodo", "Sam"));
        list.add(Iterators.iterator());
        list.add(Iterators.iterator("Gollum"));

        assertBrowsesAllOnce(
            Arrays.asList("Frodo", "Sam", "Gollum"),
            Iterators.unwrap(list.iterator())
        );
    }
}
