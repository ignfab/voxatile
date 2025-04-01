package com.ignfab.minalac.generator.parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * Annotation to make Jackson use a custom delegating deserializer for a given class.
 * This way, Jackson will create the default deserializer for the object, and then it
 * will get wrapped with the custom deserializer, delegating to the default one.
 * <p>
 * The wrapping deserializer subclass must be specified with {@link #using()},
 * otherwise this annotation has no effect.
 * <p>
 * The delegating deserializer subclass should override relevant methods, such as
 * {@link DelegatingDeserializer#deserialize(JsonParser, DeserializationContext) deserialize} or
 * {@link DelegatingDeserializer#deserializeWithType(JsonParser, DeserializationContext, TypeDeserializer) deserializeWithType}.
 * <p>
 * This is particularly useful to make a custom deserializer that can fallback to
 * the default one.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDelegateDeserialize {
    /**
     * The class of the custom delegating deserializer to use.
     * If not specified or equals to the default value ({@code DelegatingDeserializer.class}),
     * no custom deserializer will be added. Since annotations
     * in Jackson are inherited, it can be used to revert the
     * effect of this annotation for a child in a hierarchy.
     * @return Delegating deserializer class
     */
    Class<? extends DelegatingDeserializer> using() default DelegatingDeserializer.class;

    /**
     * Jackson handler to wrap deserializer of annotated beans with the requested one.
     * This should be registered in the {@link com.fasterxml.jackson.databind.ObjectMapper}:
     * <pre>{@code
     *  ObjectMapper mapper = ...;
     *  SimpleModule module = new SimpleModule("MyModule");
     *  module.setDeserializerModifier(new JsonDelegateDeserialize.BeanModifier());
     *  mapper.registerModule(module);
     * }</pre>
     */
    class BeanModifier extends BeanDeserializerModifier {
        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            JsonDelegateDeserialize annotation = beanDesc.getClassAnnotations().get(JsonDelegateDeserialize.class);
            if (annotation == null || annotation.using() == DelegatingDeserializer.class)
                return deserializer;
            try {
                return annotation.using().getConstructor(JsonDeserializer.class).newInstance(deserializer);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to construct delegating deserializer from " + annotation.using(), e);
            }
        }
    }
}
