package com.ignfab.minalac.generator.parameters.utils;

import java.io.IOException;
import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.utils.IntegerIntervals;

/**
 * A notation for a list of integer intervals.
 * <p>
 * Intervals are merged if they are contiguous.
 */
@JsonDeserialize(using = IntegerIntervalsParams.Deserializer.class)
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

    // This class is just same as IntegerIntervalsParams but without custom deserializer.
    // It avoids infinite loop deserializing an IntegerIntervalsParams from its custom deserializer.
    @JsonDeserialize
    private static class Bare extends IntegerIntervalsParams {}

    // Custom deserializer allowing usage of single value.
    //
    // This is a bit hacky and it would have been much better to add following annotation to IntegerIntervalParams:
    // @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    // But that wont work (works only on class attributes).
    public static class Deserializer extends JsonDeserializer<LinkedList<IntegerIntervalParams>> {
        @Override
        public LinkedList<IntegerIntervalParams> deserialize(JsonParser p, DeserializationContext ctxt)
            throws StreamReadException, DatabindException, IOException {

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            return mapper.readValue(p, Bare.class);
        }
    }
}



