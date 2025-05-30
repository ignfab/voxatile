package com.ignfab.minalac.generator.parameters.utils;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.utils.IntegerInterval;

/**
 * A notation for an integer interval.
 * <p>
 * Interval can be written in various ways. Canonical way:
 * {@snippet lang="yaml" :
 * intervalA:
 *   from: 2
 *   to: 5
 * }
 * For a single value interval:
 * {@snippet lang="yaml" :
 * intervalB:
 *   value: 4
*  }
 * Same intervals but with shortened notation:
 * {@snippet lang="yaml" :
 * intervalA: 2..5
 * intervalB: 4
 * }
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = IntegerIntervalParams.FallbackParams.class)
@JsonSubTypes({
    @Type(IntegerIntervalParams.FromToParams.class),
    @Type(IntegerIntervalParams.ValueParams.class)
})
public abstract class IntegerIntervalParams {
    /**
     * Creates an {@code IntegerInterval} from these parameters.
     *
     * @return {@code IntegerInterval} created out of parameters.
     */
    public abstract IntegerInterval create();

    /**
     * Validates these parameters.
     */
    public void validate() {}

    /**
     * Parameter class for "from: to:" form.
     */
    public static class FromToParams extends IntegerIntervalParams {
        /**
         * Starting value of interval (required).
         */
        public int from;
        /**
         * Ending value of interval (required).
         */
        public int to;

        @ConstructorProperties({"from", "to"})
        FromToParams(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void validate() {
            if (from > to)
                throw new IllegalArgumentException("'from' must be lesser than 'to'");
        }

        @Override
        public IntegerInterval create() {
            return new IntegerInterval(from, to);
        }
    }

    /**
     * Parameter class for "value:" form.
     */
    public static class ValueParams extends IntegerIntervalParams {
        /**
         * Unique value of interval (required).
         */
        public int value;

        @ConstructorProperties({"value"})
        ValueParams(int value) {
            this.value = value;
        }

        @Override
        public IntegerInterval create() {
            return new IntegerInterval(value, value);
        }
    }

    /**
     * Fallback parameter class for "A..B" and "C" forms.
     */
    @JsonDeserialize(using = IntegerIntervalParams.Deserializer.class)
    public static class FallbackParams extends FromToParams {

        /**
         * Creates a {@code FallbackParams}.
         *
         * @param from starting value of interval
         * @param to ending value of interval
         */
        public FallbackParams(int from, int to) {
            super(from, to);
        }
    }

     /**
     * A custom deserializer for {@code CustomParams}s.
     *
     * It maps format name to a {@code CustomParams} object.
     */
    public static class Deserializer extends ValueDeserializer<FallbackParams> {

        @Override
        public FallbackParams deserialize(JsonParser parser, DeserializationContext context) throws DatabindException {
            return stringToFallbackParams(parser.readValueAs(String.class));
        }

         /**
          * Returns a new FallbackParams from a string.
          *
          * @param string the string to transform.
          * @return a new FallbackParams.
          * */
        public static FallbackParams stringToFallbackParams(String string) {
            int index = string.indexOf("..");
            if (index == -1) {
                int value = Integer.parseInt(string.trim());
                return new FallbackParams(value, value);
            } else {
                int from = Integer.parseInt(string.substring(0, index).trim());
                int to = Integer.parseInt(string.substring(index + 2).trim());
                return new FallbackParams(from, to);
            }
        }
    }
}
