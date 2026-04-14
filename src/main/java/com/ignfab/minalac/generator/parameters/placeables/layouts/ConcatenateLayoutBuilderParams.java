package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ObjectNode;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for a {@link LayoutBuilder} concatenates different layouts along an axis
 * <p>
 * Usage example:
 * <pre>
 *   place:
 *     - ... first layout description ...
 *     - priority: 2
 *       ... second layout description ...
 *   along: x | y | z
 * </pre>
 */
public class ConcatenateLayoutBuilderParams implements LayoutBuilderParams {
    /**
     * List of layouts to concatenate.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public List<ConcatenateParams> concatenate;

    /**
     * Axis along which layouts are concatenated.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    AxisParams along;

    /**
     * Axes along which layouts are adjusted.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    List<AxisParams> adjust;

    /**
     * Creates a new {@code ConcatenateLayoutBuilderParams} out of mandatory parameters.
     * @param concatenate list of layouts to concatenate
     * @param along axis along which concatenate layouts
     */
    @ConstructorProperties({ "concatenate", "along" })
    public ConcatenateLayoutBuilderParams(List<ConcatenateParams> concatenate, AxisParams along) {
        this.concatenate = concatenate;
        this.along = along;
    }

    @Override
    public void validate() {
        if (concatenate.isEmpty())
            throw new IllegalArgumentException("Cannot be empty");
        concatenate.forEach(ConcatenateParams::validate);
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
        LayoutBuilder[] builders = new LayoutBuilder[concatenate.size()];
        int[] priorities = new int[concatenate.size()];
        for (int i = 0; i < concatenate.size(); i++)  {
            builders[i] = concatenate.get(i).layout.createBuilder(seed);
            priorities[i] = concatenate.get(i).priority;
        }

        List<Axis> adjustAxes = adjust == null
            ? Collections.emptyList()
            : adjust.stream().map(AxisParams::create).collect(Collectors.toList());

        return DefaultLayoutBuilder.concat(builders, along.create(), priorities, adjustAxes);
    }

    /**
     * Parameters for each concatenated layout.
     * <p>
     * This is basically a {@link LayoutBuilderParams} with an eventual extra {@code priority} field.
     */
    @JsonDeserialize(using = ConcatenateParams.Deserializer.class)
    public static final class ConcatenateParams {
        /**
         * Concatenated {@link LayoutBuilderParams}.
         */
        @JsonSetter(nulls = Nulls.FAIL)
        public LayoutBuilderParams layout;

        /**
         * Priority for space repartition.
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int priority = 0;

        /**
         * Validates parameters.
         */
        public void validate() {
            layout.validate();
        }

        /**
         * Custom deserializer for {@code ConcatenateParams}.
         * <p>
         * This deserializer will take care of {@code priority} field and use all other fields to deserialize into {@code layout}.
         */
        public static class Deserializer extends ValueDeserializer<ConcatenateParams> {

            @Override
            public ConcatenateParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
                JsonNode node = parser.readValueAsTree();

                if (node instanceof ObjectNode objectNode) {
                    ConcatenateParams params = new ConcatenateParams();

                    JsonNode priorityNode = objectNode.get("priority");
                    if (priorityNode != null) {
                        if (!priorityNode.isIntegralNumber())
                            throw new InputCoercionException(parser, "Priority should be a integer number", node.asToken(), Integer.class);
                        params.priority = priorityNode.asInt();
                        objectNode.remove("priority");
                    }
                    params.layout = context.readTreeAsValue(objectNode, LayoutBuilderParams.class);

                    return params;
                } else
                    throw new InputCoercionException(parser, "Placed layouts should be layouts", node.asToken(), LayoutBuilderParams.class);
            }
        }
    }
}
