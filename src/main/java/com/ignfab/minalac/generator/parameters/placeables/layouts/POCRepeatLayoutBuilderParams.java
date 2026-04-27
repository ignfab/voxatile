package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.XRepeat.class),
    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.YRepeat.class),
    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.ZRepeat.class),

    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.XOptionalRepeat.class),
    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.YOptionalRepeat.class),
    @JsonSubTypes.Type(POCRepeatLayoutBuilderParams.ZOptionalRepeat.class)
})
public abstract class POCRepeatLayoutBuilderParams implements LayoutBuilderParams {
    private LayoutBuilderParams repeat;
    private AxisParams along;
    private int atLeast = 1;
    private int atMost = Integer.MAX_VALUE;

    public POCRepeatLayoutBuilderParams(LayoutBuilderParams repeat, AxisParams along, int atLeast) {
        this.repeat = repeat;
        this.along = along;
        this.atLeast = atLeast;
    }

    @Override
    public void validate() {
        if (atLeast < 0)
            throw new IllegalArgumentException("atLeast field must be a positive integer");
        if (atMost < atLeast)
            throw new IllegalArgumentException("atMost must be greater than atLeast");
        repeat.validate();
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
        return DefaultLayoutBuilder.repeat(
            repeat.createBuilder(seed),
            along.create(),
            atLeast,
            atMost
        );
    }

    public static class XOptionalRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams xOptionalRepeat;
        @ConstructorProperties("xOptionalRepeat")
        public XOptionalRepeat(LayoutBuilderParams xOptionalRepeat) {
            super(xOptionalRepeat, AxisParams.X, 0);
        }
    }

    public static class YOptionalRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams yOptionalRepeat;
        @ConstructorProperties("yOptionalRepeat")
        public YOptionalRepeat(LayoutBuilderParams yOptionalRepeat) {
            super(yOptionalRepeat, AxisParams.Y, 0);
        }
    }

    public static class ZOptionalRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams zOptionalRepeat;
        @ConstructorProperties("zOptionalRepeat")
        public ZOptionalRepeat(LayoutBuilderParams zOptionalRepeat) {
            super(zOptionalRepeat, AxisParams.Z, 0);
        }
    }

    public static class XRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams xRepeat;
        @ConstructorProperties("xRepeat")
        public XRepeat(LayoutBuilderParams xRepeat) {
            super(xRepeat, AxisParams.X, 1);
        }
    }

    public static class YRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams yRepeat;
        @ConstructorProperties("yRepeat")
        public YRepeat(LayoutBuilderParams yRepeat) {
            super(yRepeat, AxisParams.Y, 1);
        }
    }

    public static class ZRepeat extends POCRepeatLayoutBuilderParams{
        public LayoutBuilderParams zRepeat;
        @ConstructorProperties("zRepeat")
        public ZRepeat(LayoutBuilderParams zRepeat) {
            super(zRepeat, AxisParams.Z, 1);
        }
    }
}
