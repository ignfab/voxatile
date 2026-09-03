package com.ignfab.minalac.generator.parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;
import tools.jackson.databind.util.AccessPattern;

/**
 * Annotation for params classes that are purely Java wrapper around a single field.
 * The params will be deserialized as the type of the underlying field, then the
 * wrapper object will be created using either single-arg constructor or field injection.
 * Thus, the name of the underlying field is irrelevant for params (only visible in Java).
 * <p>
 * Note: The type this annotation is applied to MUST only have one single property,
 * otherwise it will result in a runtime exception when creating the deserializer!
 * <p>
 * Direct {@code null} handling can be done either on the underlying field or where
 * the type is used. If either one or both specifies {@link com.fasterxml.jackson.annotation.Nulls#FAIL Nulls.FAIL},
 * no {@code null} will be allowed. If unspecified at usage, a {@code null} params
 * will result in a wrapper being created with a value according to the {@code null}
 * handling policy of the underlying field of that wrapping class.
 * This means that if the underlying field disallows {@code null}, an exception
 * will occur.
 * <p>
 * This is particularly useful to make a params class transparent for the user.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWrapper {
    /**
     * Jackson handler to set deserializer of annotated beans with the appropriate one.
     * This is bundled by default with the {@link com.ignfab.minalac.generator.parameters.ParamsParser.VoxatileParserModule}.
     */
    class BeanModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
            // Ensure that the annotation is present on the type
            JsonWrapper annotation = beanDescRef.getClassAnnotations().get(JsonWrapper.class);
            if (annotation == null)
                return deserializer;

            // Find a bean deserializer somewhere in the delegation tree of the original deserializer
            // It is useful to get information about the underlying property, and how to instantiate the wrapper
            BeanDeserializerBase beanDeser = findBeanDeserializerBase(deserializer);
            // Unlikely to happen as most deserializers are based on a bean deserializer, but we never know...
            if (beanDeser == null)
                throw new IllegalStateException(beanDescRef.getType() + " is not a valid wrapper (not deserialized as a bean)");

            // We must make sure there is only a single property
            if (beanDeser.getPropertyCount() != 1)
                throw new IllegalStateException(beanDescRef.getType() + " is not a valid wrapper (not a single property)");
            SettableBeanProperty property = beanDeser.findProperty(0);

            // Then we create an instantiator for the wrapper class based on the one of the original deserializer
            WrapperInstantiator instantiator = createWrapperInstantiator(config, beanDeser.getValueInstantiator(), property);
            if (instantiator == null)
                throw new IllegalStateException(beanDescRef.getType() + " is not a valid wrapper (cannot instantiate class)");

            // And finally we replace the deserializer with our custom one
            return new Deserializer(instantiator, beanDeser, property);
        }

        // A BeanDeserializerBase is needed to get a SettableBeanProperty for the underlying field
        private static BeanDeserializerBase findBeanDeserializerBase(ValueDeserializer<?> deserializer) {
            ValueDeserializer<?> deser = deserializer;
            // Traverse the delegation tree until we find a bean deserializer... or not
            while (deser != null && !(deser instanceof BeanDeserializerBase))
                deser = deser.getDelegatee();
            // The value returned can be null if no bean deserializer was present in the delegation tree
            return (BeanDeserializerBase) deser;
        }

        // Instantiation must be done with the appropriate method of the ValueInstantiator
        private static WrapperInstantiator createWrapperInstantiator(DeserializationConfig config, ValueInstantiator valueInstantiator, SettableBeanProperty property) {
            // Prefer delegation if available
            if (valueInstantiator.canCreateUsingDelegate())
                return new DelegateInstantiator(valueInstantiator);
            // Otherwise try to use the single-arg creator
            SettableBeanProperty[] args = valueInstantiator.getFromObjectArguments(config);
            if (args != null && args.length == 1 && args[0] == property)
                return new CreatorArgInstantiator(valueInstantiator);
            // Fallback to default instantiator with manual property assignment
            if (valueInstantiator.canCreateUsingDefault())
                return new DefaultInstantiator(valueInstantiator, property);
            // Or fail if no instantiation method suits our requirements
            return null;
        }
    }

    /**
     * Custom deserializer handling wrapper instantiation.
     */
    class Deserializer extends ValueDeserializer<Object> {
        private final WrapperInstantiator instantiator;
        private final BeanDeserializerBase beanDeser;
        private SettableBeanProperty property;

        Deserializer(WrapperInstantiator instantiator, BeanDeserializerBase beanDeser, SettableBeanProperty property) {
            this.instantiator = instantiator;
            this.beanDeser = beanDeser;
            this.property = property;
        }

        @Override
        public ValueDeserializer<?> getDelegatee() {
            return beanDeser;
        }

        @Override
        public void resolve(DeserializationContext context) {
            // Resolve original bean deserializer to get the fully resolved property
            beanDeser.resolve(context);
            // Find resolved property (might have changed, especially for null handling)
            property = beanDeser.findProperty(0).withName(property.getFullName());
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
            // If no contextual property available, this is not a big deal, we can still process with original name
            if (property == null)
                return this;
            // Carry over the name of the contextual property to make it
            // fully transparent for the user (i.e. in error messages)
            return new Deserializer(instantiator, beanDeser, this.property.withName(property.getFullName()));
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            // Deserialize using the property's deserializer, then wrap the result
            return wrap(context, context.findContextualValueDeserializer(property.getType(), property).deserialize(parser, context));
        }

        @Override
        public Object getNullValue(DeserializationContext context) {
            // Null value should also be wrapped, which will result in either an exception
            // if the property does not support nulls or in an "empty" wrapper
            return wrap(context, property.getNullValueProvider().getNullValue(context));
        }

        @Override
        public AccessPattern getNullAccessPattern() {
            // The wrapper should be recreated because no further assumption can be made on mutability
            return AccessPattern.DYNAMIC;
        }

        private Object wrap(DeserializationContext context, Object value) {
            try {
                return instantiator.create(context, value);
            } catch (Exception e) {
                // This generally throws an exception but maybe the situation can somehow be recovered...
                return context.handleInstantiationProblem(instantiator.getValueClass(), value, e);
            }
        }
    }

    /**
     * Simple tool to instantiate the wrapper class using the wrapped object.
     */
    abstract class WrapperInstantiator {
        /**
         * The instantiator of the wrapper class from Jackson.
         */
        protected final ValueInstantiator instantiator;

        WrapperInstantiator(ValueInstantiator instantiator) {
            this.instantiator = instantiator;
        }

        /**
         * Instantiates the wrapper object.
         * @param context Deserialization context
         * @param wrapped The wrapped object value
         * @return A new wrapper object wrapping the wrapped value
         * @throws JacksonException when unable to instantiate the class
         */
        public abstract Object create(DeserializationContext context, Object wrapped) throws JacksonException;

        /**
         * {@return the Java class of the wrapper}
         */
        public Class<?> getValueClass() {
            return instantiator.getValueClass();
        }
    }

    /**
     * Wrapper instantiator relying on the creator with a single argument.
     */
    class CreatorArgInstantiator extends WrapperInstantiator {
        CreatorArgInstantiator(ValueInstantiator instantiator) {
            super(instantiator);
        }

        @Override
        public Object create(DeserializationContext context, Object wrapped) throws JacksonException {
            return instantiator.createFromObjectWith(context, new Object[] { wrapped });
        }
    }

    /**
     * Wrapper instantiator relying on the default creator and then mutating the property.
     */
    class DefaultInstantiator extends WrapperInstantiator {
        private final SettableBeanProperty property;

        DefaultInstantiator(ValueInstantiator instantiator, SettableBeanProperty property) {
            super(instantiator);
            this.property = property;
        }

        @Override
        public Object create(DeserializationContext context, Object wrapped) throws JacksonException {
            Object wrapper = instantiator.createUsingDefaultOrWithoutArguments(context);
            property.set(context, wrapper, wrapped);
            return wrapper;
        }
    }

    /**
     * Wrapper instantiator relying on Jackson's own delegation-based instantiation.
     */
    class DelegateInstantiator extends WrapperInstantiator {
        DelegateInstantiator(ValueInstantiator instantiator) {
            super(instantiator);
        }

        @Override
        public Object create(DeserializationContext context, Object wrapped) throws JacksonException {
            return instantiator.createUsingDelegate(context, wrapped);
        }
    }
}
