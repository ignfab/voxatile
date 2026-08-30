package com.ignfab.minalac.generator.parameters;

import java.awt.Color;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

/**
 * A custom deserializer for {@code Color}s.
 */
public class ColorDeserializer extends ValueDeserializer<Color> {

    @Override
    public Color deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode node = parser.readValueAsTree();

        if (node.isString()) {
            String string = node.asString();
            try {
                return Color.decode(string);
            } catch (NumberFormatException e) {
                return (Color) context.handleWeirdStringValue(Color.class, string, "String value %s cannot be interpreted as a color", string);
            }
        }

        if (node.isArray()) {
            int size = node.size();
            if (size < 3 || size > 4)
                return context.reportInputMismatch(Color.class, "Color array must have 3 or 4 components, got %d.", size);
            if (!node.get(0).isInt() || !node.get(1).isInt() || !node.get(2).isInt() || (size == 4 && !node.get(3).isInt()))
                return context.reportInputMismatch(Color.class, "Color array must contain only integer values.");

            int red = node.get(0).asInt();
            int green = node.get(1).asInt();
            int blue = node.get(2).asInt();
            int alpha = (size == 4) ? node.get(3).asInt() : 255;

            return new Color(red, green, blue, alpha);
        }

        return (Color) context.reportInputMismatch(Color.class, "Expected a hex String or an RGB(A) Array, but got %s.", node.getNodeType());
    }
}
