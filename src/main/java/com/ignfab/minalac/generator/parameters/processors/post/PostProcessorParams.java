package com.ignfab.minalac.generator.parameters.processors.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.ignfab.minalac.generator.parameters.JsonDelegateDeserialize;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link PostProcessor}.
 */
@JsonDelegateDeserialize(using = PostProcessorParams.Deserializer.class)
public abstract class PostProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@link PostProcessor}.
     *
     * @return the created {@link PostProcessor}
     */
    public abstract PostProcessor<?, ?> create();

    /**
     * Custom deserializer to handle list of post processors seamlessly.
     */
    public static class Deserializer extends DelegatingDeserializer {
        /**
         * Creates a new instance.
         * @param delegate The default deserializer.
         */
        public Deserializer(JsonDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new Deserializer(delegate);
        }

        @Override
        public Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException {
            if (jsonParser.isExpectedStartArrayToken()) {
                List<PostProcessorParams> sequence = new ArrayList<>();
                while (jsonParser.nextToken() != JsonToken.END_ARRAY)
                    sequence.add(jsonParser.readValueAs(PostProcessorParams.class));
                return new SequentialPostProcessorParams(sequence);
            }
            return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
        }
    }
}
