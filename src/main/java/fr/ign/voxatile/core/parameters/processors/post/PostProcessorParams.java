package fr.ign.voxatile.core.parameters.processors.post;

import java.util.ArrayList;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.JsonDelegateDeserialize;
import fr.ign.voxatile.core.parameters.PolymorphicParams;
import fr.ign.voxatile.core.processors.post.PostProcessor;

/**
 * Parameters for {@link PostProcessor}.
 */
@JsonDelegateDeserialize(using = PostProcessorParams.Deserializer.class)
public abstract class PostProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@link PostProcessor}.
     *
     * @param generation the generation context
     * @return the created {@link PostProcessor}
     */
    public abstract PostProcessor<?, ?> create(Generation generation);

    /**
     * Custom deserializer to handle list of post processors seamlessly.
     */
    public static class Deserializer extends DelegatingDeserializer {
        /**
         * Creates a new instance.
         * @param delegate The default deserializer.
         */
        public Deserializer(ValueDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) {
            if (parser.isExpectedStartArrayToken()) {
                List<PostProcessorParams> sequence = new ArrayList<>();
                while (parser.nextToken() != JsonToken.END_ARRAY)
                    sequence.add(parser.readValueAs(PostProcessorParams.class));
                return new SequentialPostProcessorParams(sequence);
            }
            return super.deserializeWithType(parser, context, typeDeserializer);
        }
    }
}
