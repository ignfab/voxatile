package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({ "checkstyle:VisibilityModifier", "checkstyle:JavadocVariable" }) // For nested testing params classes
public class JsonWrapperTest {
    @Test
    void testBeanDeserializerModifier() {
        // Simple case (with primitive wrapper)
        IntWrapper intWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(IntWrapper.class, "7"));
        assertEquals(7, intWrapper.i);

        // Simple case (with object wrapper)
        StringWrapper stringWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(StringWrapper.class, "toto"));
        assertEquals("toto", stringWrapper.wrappedStr);

        // Null failing policy handling
        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(StringWrapper.class, "null"));

        // Simple case (with list wrapper)
        ListWrapper listWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(ListWrapper.class, "[3, -7]"));
        assertEquals(
            List.of(3, -7),
            listWrapper.list.stream().map(w -> w.i).toList()
        );

        // Null as empty policy handling
        ListWrapper emptyListWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(ListWrapper.class, "null"));
        assertNotNull(emptyListWrapper.list);
        assertTrue(emptyListWrapper.list.isEmpty());

        // Special features handling
        ListWrapper singletonListWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(ListWrapper.class, "-3"));
        assertEquals(1, singletonListWrapper.list.size());
        assertEquals(-3, singletonListWrapper.list.get(0).i);

        // Creator instantiation
        CreatorWrapper creatorWrapper = assertDoesNotThrow(() -> ParamsTester.deserialize(CreatorWrapper.class, "true"));
        assertTrue(creatorWrapper.arg);

        // Invalid wrapper class (multiple properties)
        assertThrows(IllegalStateException.class, () -> ParamsTester.deserialize(InvalidWrapper1.class, "42"));

        // Invalid wrapper class (no instantiation method)
        assertThrows(IllegalStateException.class, () -> ParamsTester.deserialize(InvalidWrapper2.class, "text"));

        // Wrapper usage
        DummyParams dummyParams = assertDoesNotThrow(() -> ParamsTester.deserialize(DummyParams.class, "wrapperProperty: str"));
        assertEquals("str", dummyParams.wrapperProperty.wrappedStr);

        // Validates that the wrapped property name was overridden by the wrapper one in errors
        JacksonException e = assertThrows(JacksonException.class, () -> ParamsTester.deserialize(DummyParams.class, "wrapperProperty: []"));
        List<JacksonException.Reference> path = e.getPath();
        assertEquals("wrapperProperty", path.get(path.size() - 1).getPropertyName());
    }

    // Unless otherwise specified, all wrapper classes are instantiated using the DefaultInstantiator

    // Very basic primitive wrapper
    @JsonWrapper
    public static class IntWrapper {
        public int i;
    }

    // Simple wrapper with null policy
    @JsonWrapper
    public static class StringWrapper {
        @JsonSetter(nulls = Nulls.FAIL)
        public String wrappedStr;
    }

    // Evolved wrapper with special feature
    @JsonWrapper
    public static class ListWrapper {
        @JsonSetter(nulls = Nulls.AS_EMPTY, contentNulls = Nulls.FAIL)
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<IntWrapper> list;
    }

    // Simple wrapper with creator
    // This wrapper class is instantiated with the CreatorArgInstantiator
    @JsonWrapper
    public static class CreatorWrapper {
        public boolean arg;

        @ConstructorProperties("arg")
        public CreatorWrapper(boolean arg) {
            this.arg = arg;
        }
    }

    // Illegal wrapper with multiple properties
    @JsonWrapper
    public static class InvalidWrapper1 {
        public int x;
        public int y;
    }

    // Illegal wrapper with no instantiation method (only constructor available requires an int but property is a string)
    @JsonWrapper
    public static class InvalidWrapper2 {
        public String value;

        public InvalidWrapper2(int ignored) {}
    }

    // Dummy class using a wrapper
    public static class DummyParams {
        public StringWrapper wrapperProperty;
    }
}
