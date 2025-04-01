package com.ignfab.minalac.generator.parameters;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDelegateDeserializeTest {
    @Test
    void testBeanDeserializerModifier() throws JsonProcessingException {
        TestingDeserializer.flag = false;
        ParamsTester.deserialize(AnnotatedClass.class, "{}");
        assertTrue(TestingDeserializer.flag, "Deserializer should have been called");

        TestingDeserializer.flag = false;
        ParamsTester.deserialize(ReannotatedChildOfAnnotatedClass.class, "{}");
        assertFalse(TestingDeserializer.flag, "Deserializer should not have been called");
    }

    public static class TestingDeserializer extends DelegatingDeserializer {
        private static boolean flag;

        public TestingDeserializer(JsonDeserializer<?> delegate) {
            super(delegate);
        }

        @Override
        protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> delegate) {
            return new TestingDeserializer(delegate);
        }

        @Override
        public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            flag = true;
            return super.deserialize(jsonParser, deserializationContext);
        }
    }

    @JsonDelegateDeserialize(using = TestingDeserializer.class)
    public static class AnnotatedClass {}

    @JsonDelegateDeserialize
    public static class ReannotatedChildOfAnnotatedClass extends AnnotatedClass {}
}
