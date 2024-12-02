package com.ignfab.minalac.generator.parameters.common;

import java.util.LinkedList;

import com.ignfab.minalac.generator.utils.IntegerIntervals;

/**
 * A notation for a list of integer intervals.
 * <p>
 * Intervals are merged if they are contiguous.
 * <p>
 * To allow a single object to be deserialized as a list, add following annotation to your fields:
 * <pre>
 * @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
 * </pre>
 */
// It would have been much better to put @JsonFormat here but that does not work.
 public class IntegerIntervalsParams extends LinkedList<IntegerIntervalParams> {
    /**
     * Creates an empty list.
     */
    public IntegerIntervalsParams() {}

    /**
     * Creation of a list with a first interval.
     *
     * @param begin begining of interval
     * @param end ending of interval
     */
    public IntegerIntervalsParams(int begin, int end) {
        this.add(new IntegerIntervalParams.FallbackParams(begin, end));
    }

    /**
     * Validates intervals list params.
     */
    public void validate() {
        for (IntegerIntervalParams interval : this)
            interval.validate();
    }

    /**
     * Creates {@link IntegerIntervals} from params.
     *
     * @return resulting {@link IntegerIntervals}.
     */
    public IntegerIntervals create() {
        IntegerIntervals result = new IntegerIntervals();
        for (IntegerIntervalParams interval : this)
            result.add(interval.create());
        return result;
    }
}


