package com.ignfab.minalac.generator.parameters;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

@SuppressWarnings({ "checkstyle:MissingJavadocType", "checkstyle:MissingJavadocMethod", "checkstyle:JavadocVariable" })
@JsonWrapper
public class DummyParams {
    public A a;
    @JsonSetter(nulls = Nulls.FAIL)
    public B b;
    public C c;
    @JsonSetter(nulls = Nulls.SKIP)
    public D d = new D();
    @JsonWrapper.DirectProperty
    @JsonSetter(nulls = Nulls.SKIP)
    public int e;

    @ConstructorProperties({ "b", "c" })
    public DummyParams(B b, C c) {
        this.b = b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "DummyParams{a=%s, b=%s, c=%s, d=%s, e=%s}".formatted(a, b, c, d, e);
    }

    public static class A {
        public String a;

        @Override
        public String toString() {
            return "A{a='%s'}".formatted(a);
        }
    }

    public static class B {
        public int b1;
        public int b2;

        @Override
        public String toString() {
            return "B{b1=%s, b2=%s}".formatted(b1, b2);
        }
    }

    public static class C {
        public String a;
        @JsonSetter(nulls = Nulls.FAIL)
        public String c;

        @ConstructorProperties("c")
        public C(String c) {
            this.c = c;
        }

        @Override
        public String toString() {
            return "C{a='%s', c='%s'}".formatted(a, c);
        }
    }

    public static class D {
        @JsonSetter(nulls = Nulls.SKIP)
        public boolean d = false;

        @Override
        public String toString() {
            return "D{d=%s}".formatted(d);
        }
    }
}
