package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;
import java.util.List;

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

import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for a {@link LayoutBuilder} places different layouts along an axis
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
    @JsonSetter(nulls = Nulls.FAIL)
    public List<PlaceParams> concatenate;

    @JsonSetter(nulls = Nulls.FAIL)
    AxisParams along;

    @ConstructorProperties({ "concatenate", "along" })
    public ConcatenateLayoutBuilderParams(List<PlaceParams> concatenate, AxisParams along) {
        this.concatenate = concatenate;
        this.along = along;
    }

    @Override
    public void validate() {
        if (concatenate.isEmpty())
            throw new IllegalArgumentException("Cannot be empty");
        concatenate.forEach(PlaceParams::validate);
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
        LayoutBuilder[] builders = new LayoutBuilder[concatenate.size()];
        int[] priorities = new int[concatenate.size()];
        for (int i = 0; i < concatenate.size(); i++)  {
            builders[i] = concatenate.get(i).layout.createBuilder(seed);
            priorities[i] = concatenate.get(i).priority;
        }

        return DefaultLayoutBuilder.priority(builders, along.create(), priorities);
    }

    @JsonDeserialize(using = PlaceParams.Deserializer.class)
    public static final class PlaceParams {
        @JsonSetter(nulls = Nulls.FAIL)
        public LayoutBuilderParams layout;

        @JsonSetter(nulls = Nulls.SKIP)
        public int priority = 0;

        public void validate() {
            layout.validate();
        }

        public static class Deserializer extends ValueDeserializer<PlaceParams> {

            @Override
            public PlaceParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
                JsonNode node = parser.readValueAsTree();

                if (node instanceof ObjectNode objectNode) {
                    PlaceParams params = new PlaceParams();

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
