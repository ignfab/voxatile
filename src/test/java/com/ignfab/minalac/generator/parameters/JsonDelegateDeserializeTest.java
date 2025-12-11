package com.ignfab.minalac.generator.parameters;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDelegateDeserializeTest {
    @Test
    void testBeanDeserializerModifier() throws JacksonException {
        TestingDeserializer.flag = false;
        ParamsTester.deserialize(AnnotatedClass.class, "{}");
        assertTrue(TestingDeserializer.flag, "Deserializer should have been called");

        TestingDeserializer.flag = false;
        ParamsTester.deserialize(ReannotatedChildOfAnnotatedClass.class, "{}");
        assertFalse(TestingDeserializer.flag, "Deserializer should not have been called");
    }

    public static class TestingDeserializer extends DelegatingDeserializer {
        private static boolean flag;

        public TestingDeserializer(ValueDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> delegate) {
            return new TestingDeserializer(delegate);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) {
            flag = true;
            return super.deserialize(parser, context);
        }
    }

    @JsonDelegateDeserialize(using = TestingDeserializer.class)
    public static class AnnotatedClass {}

    @JsonDelegateDeserialize
    public static class ReannotatedChildOfAnnotatedClass extends AnnotatedClass {}
}
