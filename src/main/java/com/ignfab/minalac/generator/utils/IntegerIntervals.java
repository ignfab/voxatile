package com.ignfab.minalac.generator.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of integer intervals.
 */
public class IntegerIntervals implements Iterable<IntegerInterval> {
    private List<IntegerInterval> intervals = new LinkedList<>();

    @Override
    public Iterator<IntegerInterval> iterator() {
        return intervals.iterator();
    };

    /**
     * Creates a new {@code IntegerIntervals} with overlapping intervals merged.
     *
     * @return resulting {@code IntegerIntervals}.
     */
    public IntegerIntervals merged() {
        Collections.sort(intervals);

        IntegerIntervals result = new IntegerIntervals();
        Iterator<IntegerInterval> it = iterator();

        IntegerInterval newInterval;
        IntegerInterval oldInterval;

        if (!it.hasNext())
            return result;

        newInterval = it.next();

        while (it.hasNext()) {
            oldInterval = it.next();
            if (newInterval.overlaps(oldInterval))
                newInterval =  new IntegerInterval(
                    Math.min(newInterval.begin(), oldInterval.begin()),
                    Math.max(newInterval.end(), oldInterval.end())
                );
            else {
                result.add(newInterval);
                newInterval = oldInterval;
            }
        }
        result.add(newInterval);
        return result;
    }

    /**
     * Adds a new interval to the {@code IntegerIntervals}.
     *
     * @param interval interval to add
     */
    public void add(IntegerInterval interval) {
        intervals.add(interval);
    }

    /**
     * Adds a new interval to the {@code IntegerIntervals}.
     *
     * @param begin start value of the interval to add
     * @param end end value of the interval to add
     */
    public void add(int begin, int end) {
        add(new IntegerInterval(begin, end));
    }
}
