package com.ignfab.minalac.generator.parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.bean.BeanDeserializerBase;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.util.AccessPattern;

/**
 * Annotation for params classes that are purely Java wrapper around other params.
 * The params will be deserialized as the type of the underlying fields (once for each),
 * then the wrapper object will be created using either constructor or field injection.
 * Thus, the name of the underlying fields are irrelevant for params (only visible in Java).
 * <p>
 * If the type this annotation is applied to only have one single property, it will be
 * deserialized once as the type of that property, then wrapped. Otherwise, if multiple
 * properties are present, the value will be deserialized multiple times, once per property.
 * Either way, {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} will be honored.
 * In case when there are multiple properties, it will fail if and only if a property is
 * never used by any sub-deserializer, but each one will be allowed to ignore unknown.
 * <p>
 * Sub-properties can be annotated with {@link DirectProperty} to mark them as "direct".
 * Such properties are not wrapped, and thus very similar to normal properties on params.
 * <p>
 * Direct {@code null} handling can be done either on the underlying fields or where
 * the type is used. If either one or both specifies {@link com.fasterxml.jackson.annotation.Nulls#FAIL Nulls.FAIL},
 * no {@code null} will be allowed. If unspecified at usage, a {@code null} params
 * will result in a wrapper being created with values according to the {@code null}
 * handling policy of the underlying fields of that wrapping class.
 * This means that if one underlying field disallows {@code null}, an exception
 * will occur.
 * <p>
 * This is particularly useful to make a params class transparent for the user.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonWrapper {
    /**
     * Direct properties are sub-properties of wrapper params which are not wrapped.
     * This means that the property will be deserialized like a normal property,
     * as if it was not inside a wrapper params.
     * <p>
     * This is particularly useful to add a property to a multi-wrapper params
     * without needing to create a class also wrapping that property.
     */
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @interface DirectProperty {}

    /**
     * Jackson handler to set deserializer of annotated beans with the appropriate one.
     * This is bundled by default with the {@link com.ignfab.minalac.generator.parameters.ParamsParser.MinalacParserModule}.
     */
    class BeanModifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
            // Ensure that the annotation is present on the type
            if (!beanDescRef.getClassAnnotations().has(JsonWrapper.class))
                return deserializer;

            // Find a bean deserializer somewhere in the delegation tree of the original deserializer
            // It is useful to get information about the underlying property, and how to instantiate the wrapper
            BeanDeserializerBase beanDeser = findBeanDeserializerBase(deserializer);
            // Unlikely to happen as most deserializers are based on a bean deserializer, but we never know...
            if (beanDeser == null)
                throw InvalidDefinitionException.from((JsonParser) null, beanDescRef.getType() + " is not a valid wrapper (not deserialized as a bean)", beanDescRef.getType());

            // Then we create an instantiator for the wrapper class based on the one of the original deserializer
            WrapperInstantiator instantiator = createWrapperInstantiator(config, beanDeser.getValueInstantiator(), beanDeser.getPropertyCount());

            // And finally we replace the deserializer with our custom one
            return new Deserializer(instantiator, beanDeser, null);
        }

        // A BeanDeserializerBase is needed to get a SettableBeanProperty for the underlying field(s)
        private static BeanDeserializerBase findBeanDeserializerBase(ValueDeserializer<?> deserializer) {
            ValueDeserializer<?> deser = deserializer;
            // Traverse the delegation tree until we find a bean deserializer... or not
            while (deser != null && !(deser instanceof BeanDeserializerBase))
                deser = deser.getDelegatee();
            // The value returned can be null if no bean deserializer was present in the delegation tree
            return (BeanDeserializerBase) deser;
        }

        // Instantiation must be done with the appropriate method of the ValueInstantiator
        private static WrapperInstantiator createWrapperInstantiator(DeserializationConfig config, ValueInstantiator valueInstantiator, int properties) {
            // Count creator properties, if any
            SettableBeanProperty[] args = valueInstantiator.getFromObjectArguments(config);
            int creatorProperties = args == null ? 0 : args.length;

            // Select the most appropriate instantiator
            WrapperInstantiator instantiator;
            if (creatorProperties > 0 && valueInstantiator.canCreateFromObjectWith())
                instantiator = new CreatorArgInstantiator(valueInstantiator, creatorProperties);
            else if (valueInstantiator.canCreateUsingDefault())
                instantiator = new DefaultInstantiator(valueInstantiator);
            else
                instantiator = new MissingInstantiator(valueInstantiator);

            // Handle non-creator properties, if any
            return properties > creatorProperties ? new PostInstantiator(instantiator) : instantiator;
        }
    }

    /**
     * Custom deserializer handling wrapper instantiation.
     */
    class Deserializer extends ValueDeserializer<Object> {
        private final WrapperInstantiator instantiator;
        private final BeanDeserializerBase beanDeser;
        private final PropertyName contextualName;
        private List<SettableBeanProperty> properties;
        private Set<String> subPropertyNames;

        Deserializer(WrapperInstantiator instantiator, BeanDeserializerBase beanDeser, PropertyName contextualName) {
            this.instantiator = instantiator;
            this.beanDeser = beanDeser;
            this.contextualName = contextualName;
        }

        @Override
        public ValueDeserializer<?> getDelegatee() {
            return beanDeser;
        }

        @Override
        public void resolve(DeserializationContext context) {
            // Resolve original bean deserializer to get the fully resolved property
            beanDeser.resolve(context);

            // Find resolved properties
            int propertyCount = beanDeser.getPropertyCount();
            properties = new ArrayList<>(propertyCount);
            subPropertyNames = new HashSet<>();
            for (Iterator<SettableBeanProperty> it = beanDeser.properties(); it.hasNext();) {
                SettableBeanProperty property = it.next();

                // Direct properties behave similar to normal (not wrapped) properties
                boolean isDirect = property.getAnnotation(DirectProperty.class) != null;

                // Carry over contextual property name only for non-direct properties
                if (!isDirect && contextualName != null)
                    property = property.withName(contextualName);

                // Compute value deserializer for each property
                ValueDeserializer<?> deser = context.findContextualValueDeserializer(property.getType(), property);

                // Handle direct properties first, then bean-based properties, only in multi-params mode
                if (isDirect) {
                    // Collect property name and aliases
                    Set<String> names = new HashSet<>();
                    names.add(property.getName());
                    for (PropertyName alias : property.findAliases(context.getConfig()))
                        names.add(alias.getSimpleName());
                    // And remember them to later check for unknown properties
                    subPropertyNames.addAll(names);

                    // The property will be handled with the direct property deserializer
                    deser = new DirectPropertyDeserializer(deser, property, names);
                } else if (propertyCount > 1 && deser instanceof BeanDeserializerBase bd) {
                    // To prevent failure on multi-params deserialization
                    deser = bd.withIgnoreAllUnknown(true);

                    // Collect sub-property names to later check for unknown properties
                    bd.collectAllPropertyNamesTo(subPropertyNames);
                    bd.properties().forEachRemaining(prop -> {
                        for (PropertyName alias : prop.findAliases(context.getConfig()))
                            subPropertyNames.add(alias.getSimpleName());
                    });
                }
                properties.add(property.withValueDeserializer(deser));
            }
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
            // If no contextual property available, this is not a big deal, we can still process with original name
            if (property == null)
                return this;
            // Carry over the name of the contextual property to make it
            // fully transparent for the user (i.e. in error messages)
            Deserializer deser = new Deserializer(instantiator, beanDeser, property.getFullName());
            // Contextualized deserializer must be resolved before it can be used
            deser.resolve(context);
            return deser;
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            // Deserialize the value into a node, then wrap the result
            // This buffering is required to deserialize multiple properties from this single value
            TreeNode node = parser.readValueAsTree();
            Object wrapper = instantiator.create(context, node, properties);
            // Check for unknown properties after successful deserialization only
            failOnUnknownProperties(context, node, wrapper);
            return wrapper;
        }

        @Override
        public Object getNullValue(DeserializationContext context) {
            // Null value should also be wrapped, which will result in either an exception
            // if any property does not support nulls or in an "empty" wrapper
            return instantiator.create(context, NullNode.getInstance(), properties);
        }

        @Override
        public AccessPattern getNullAccessPattern() {
            // The wrapper should be recreated because no further assumption can be made on mutability
            return AccessPattern.DYNAMIC;
        }

        private void failOnUnknownProperties(DeserializationContext context, TreeNode node, Object wrapper) {
            // Only check if it is really needed
            if (properties.size() <= 1 || !context.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))
                return;
            for (String name : node.propertyNames())
                if (!subPropertyNames.contains(name))
                    throw UnrecognizedPropertyException.from(parserOnProperty(node, context, name), wrapper, name, new HashSet<>(subPropertyNames));
        }

        // Recreate a parser located right on the given property name
        private static JsonParser parserOnProperty(TreeNode node, DeserializationContext context, String propertyName) {
            JsonParser parser = node.traverse(context);
            // Find the given property, or reach the end (should never happen in this case)
            while (!parser.isClosed() && !propertyName.equals(parser.nextName()))
                parser.skipChildren();
            return parser;
        }
    }

    /**
     * Custom deserializer handling {@linkplain DirectProperty direct properties}.
     */
    class DirectPropertyDeserializer extends DelegatingDeserializer {
        private final SettableBeanProperty property;
        private final Set<String> propertyNames;

        DirectPropertyDeserializer(ValueDeserializer<?> deserializer, SettableBeanProperty property, Set<String> propertyNames) {
            super(deserializer);
            this.property = property;
            this.propertyNames = propertyNames;
        }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new DirectPropertyDeserializer(delegate, property, propertyNames);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            return deser(parser, context, null);
        }

        @Override
        public Object deserializeWithType(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            return deser(parser, context, typeDeserializer);
        }

        private Object deser(JsonParser parser, DeserializationContext context, TypeDeserializer typeDeserializer) throws JacksonException {
            // Verify that we have an object to deserialize
            if (!parser.isExpectedStartObjectToken()) {
                JsonToken token = parser.currentToken();
                return context.handleUnexpectedToken(property.getType(), token, parser,
                    "Direct property '%s' can only be deserialized from an object, got %s (token: %s)",
                    property.getName(), JsonToken.valueDescFor(token), token);
            }

            // Read until a condition below returns or the end is reached
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();

                // End of the main object
                if (token == JsonToken.END_OBJECT)
                    return null;

                // Unexpected token, should always be a property name at this point
                if (token != JsonToken.PROPERTY_NAME)
                    return context.handleUnexpectedToken(property.getType(), token, parser, "Expected %s token, got %s", JsonToken.PROPERTY_NAME, token);
                String name = parser.currentName();
                parser.nextToken();

                // If the name matches our property, delegate real deserialization duty
                if (propertyNames.contains(name))
                    return typeDeserializer == null ? super.deserialize(parser, context) : super.deserializeWithType(parser, context, typeDeserializer);

                // Otherwise just skip the children and loop back
                parser.skipChildren();
            }

            // When the end of the parser is reached
            return null;
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
         * @param value The wrapped value
         * @param properties The properties of the wrapper
         * @return A new wrapper object wrapping the wrapped value
         * @throws JacksonException when unable to instantiate the class
         */
        public abstract Object create(DeserializationContext context, TreeNode value, List<SettableBeanProperty> properties) throws JacksonException;

        static JsonParser parser(TreeNode node, DeserializationContext context) {
            JsonParser parser = node.traverse(context);
            // Advance to the first token
            parser.nextToken();
            return parser;
        }
    }

    /**
     * Wrapper instantiator relying on the creator with a single argument.
     */
    class CreatorArgInstantiator extends WrapperInstantiator {
        private final int creatorProperties;

        CreatorArgInstantiator(ValueInstantiator instantiator, int creatorProperties) {
            super(instantiator);
            this.creatorProperties = creatorProperties;
        }

        @Override
        public Object create(DeserializationContext context, TreeNode value, List<SettableBeanProperty> properties) throws JacksonException {
            Object[] arguments = new Object[creatorProperties];
            for (SettableBeanProperty property : properties)
                if (property.isCreatorProperty())
                    arguments[property.getCreatorIndex()] = property.deserialize(parser(value, context), context);
            return instantiator.createFromObjectWith(context, arguments);
        }
    }

    /**
     * Wrapper instantiator relying on the default creator.
     */
    class DefaultInstantiator extends WrapperInstantiator {
        DefaultInstantiator(ValueInstantiator instantiator) {
            super(instantiator);
        }

        @Override
        public Object create(DeserializationContext context, TreeNode value, List<SettableBeanProperty> properties) throws JacksonException {
            return instantiator.createUsingDefaultOrWithoutArguments(context);
        }
    }

    /**
     * Wrapper instantiator failing with missing instantiator.
     */
    class MissingInstantiator extends WrapperInstantiator {
        MissingInstantiator(ValueInstantiator instantiator) {
            super(instantiator);
        }

        @Override
        public Object create(DeserializationContext context, TreeNode value, List<SettableBeanProperty> properties) throws JacksonException {
            return context.handleMissingInstantiator(instantiator.getValueClass(), instantiator, parser(value, context), "Unable to instantiate wrapper class");
        }
    }

    /**
     * Wrapper instantiator relying on another instantiator and then mutating the other properties.
     */
    class PostInstantiator extends WrapperInstantiator {
        private final WrapperInstantiator wrapperInstantiator;

        PostInstantiator(WrapperInstantiator instantiator) {
            super(instantiator.instantiator);
            this.wrapperInstantiator = instantiator;
        }

        @Override
        public Object create(DeserializationContext context, TreeNode value, List<SettableBeanProperty> properties) throws JacksonException {
            Object wrapper = wrapperInstantiator.create(context, value, properties);
            for (SettableBeanProperty property : properties) {
                if (!property.isCreatorProperty()) {
                    JsonParser parser = parser(value, context);
                    parser.assignCurrentValue(wrapper);
                    property.deserializeAndSet(parser, context, wrapper);
                }
            }
            return wrapper;
        }
    }
}
