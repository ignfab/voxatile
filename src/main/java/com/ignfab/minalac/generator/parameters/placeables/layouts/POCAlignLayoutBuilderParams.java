package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ObjectNode;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.random.Seed;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.XAlign.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.XAlignY.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.XAlignZ.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.XAlignYZ.class),

    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.YAlign.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.YAlignX.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.YAlignZ.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.YAlignXZ.class),

    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.ZAlign.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.ZAlignX.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.ZAlignY.class),
    @JsonSubTypes.Type(POCAlignLayoutBuilderParams.ZAlignXY.class)
})
public abstract class POCAlignLayoutBuilderParams implements LayoutBuilderParams {
    private final List<AlignParams> align;
    private final AxisParams along;
    private final List<AxisParams> adjust;

    public POCAlignLayoutBuilderParams(List<AlignParams> align, AxisParams along, List<AxisParams> adjust) {
        this.align = align;
        this.along = along;
        this.adjust = adjust;
    }

    @Override
    public void validate() {
        if (align.isEmpty())
            throw new IllegalArgumentException("Cannot be empty");
        align.forEach(AlignParams::validate);
    }

    @Override
    public LayoutBuilder createBuilder(Seed seed) throws UnbuildableException {
        LayoutBuilder[] builders = new LayoutBuilder[align.size()];
        int[] priorities = new int[align.size()];
        for (int i = 0; i < align.size(); i++)  {
            builders[i] = align.get(i).layout.createBuilder(seed);
            priorities[i] = align.get(i).priority;
        }

        List<Axis> adjustAxes = adjust == null
            ? Collections.emptyList()
            : adjust.stream().map(AxisParams::create).collect(Collectors.toList());

        return DefaultLayoutBuilder.concat(builders, along.create(), priorities, adjustAxes);
    }


    @JsonDeserialize(using = AlignParams.Deserializer.class)
    public static final class AlignParams {
        @JsonSetter(nulls = Nulls.FAIL)
        public LayoutBuilderParams layout;

        @JsonSetter(nulls = Nulls.SKIP)
        public int priority = 0;

        public void validate() {
            layout.validate();
        }

        public static class Deserializer extends ValueDeserializer<AlignParams> {

            @Override
            public AlignParams deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
                JsonNode node = parser.readValueAsTree();

                if (node instanceof ObjectNode objectNode) {
                    AlignParams params = new AlignParams();

                    JsonNode priorityNode = objectNode.get("priority");
                    if (priorityNode != null) {
                        if (!priorityNode.isIntegralNumber())
                            throw new InputCoercionException(parser, "Priority should be a integer number", node.asToken(), Integer.class);
                        params.priority = priorityNode.asInt();
                        objectNode.remove("priority");
                    }
                    params.layout = context.readTreeAsValue(objectNode, LayoutBuilderParams.class);

                    return params;
                } else
                    throw new InputCoercionException(parser, "Placed layouts should be layouts", node.asToken(), LayoutBuilderParams.class);
            }
        }
    }

    public static class XAlign extends POCAlignLayoutBuilderParams {
        public List<AlignParams> xAlign;
        @ConstructorProperties("xAlign")
        public XAlign(List<AlignParams> xAlign) {
            super(xAlign, AxisParams.X, null);
        }
    }

    public static class XAlignY extends POCAlignLayoutBuilderParams {
        public List<AlignParams> xAlignY;
        @ConstructorProperties("xAlignY")
        public XAlignY(List<AlignParams> xAlignY) {
            super(xAlignY, AxisParams.X, List.of(AxisParams.Y));
        }
    }

    public static class XAlignZ extends POCAlignLayoutBuilderParams {
        public List<AlignParams> xAlignZ;
        @ConstructorProperties("xAlignZ")
        public XAlignZ(List<AlignParams> xAlignZ) {
            super(xAlignZ, AxisParams.X, List.of(AxisParams.Z));
        }
    }

    public static class XAlignYZ extends POCAlignLayoutBuilderParams {
        public List<AlignParams> xAlignYZ;
        @ConstructorProperties("xAlignYZ")
        public XAlignYZ(List<AlignParams> xAlignYZ) {
            super(xAlignYZ, AxisParams.X, List.of(AxisParams.Y, AxisParams.Z));
        }
    }

    //

    public static class YAlign extends POCAlignLayoutBuilderParams {
        public List<AlignParams> yAlign;
        @ConstructorProperties("yAlign")
        public YAlign(List<AlignParams> yAlign) {
            super(yAlign, AxisParams.Y, null);
        }
    }

    public static class YAlignX extends POCAlignLayoutBuilderParams {
        public List<AlignParams> yAlignX;
        @ConstructorProperties("yAlignX")
        public YAlignX(List<AlignParams> yAlignX) {
            super(yAlignX, AxisParams.Y, List.of(AxisParams.X));
        }
    }

    public static class YAlignZ extends POCAlignLayoutBuilderParams {
        public List<AlignParams> yAlignZ;
        @ConstructorProperties("yAlignZ")
        public YAlignZ(List<AlignParams> yAlignZ) {
            super(yAlignZ, AxisParams.Y, List.of(AxisParams.Z));
        }
    }

    public static class YAlignXZ extends POCAlignLayoutBuilderParams {
        public List<AlignParams> yAlignXZ;
        @ConstructorProperties("yAlignXZ")
        public YAlignXZ(List<AlignParams> yAlignXZ) {
            super(yAlignXZ, AxisParams.Y, List.of(AxisParams.X, AxisParams.Z));
        }
    }

    //
    public static class ZAlign extends POCAlignLayoutBuilderParams {
        public List<AlignParams> zAlign;
        @ConstructorProperties("zAlign")
        public ZAlign(List<AlignParams> zAlign) {
            super(zAlign, AxisParams.Z, null);
        }
    }

    public static class ZAlignX extends POCAlignLayoutBuilderParams {
        public List<AlignParams> zAlignX;
        @ConstructorProperties("zAlignX")
        public ZAlignX(List<AlignParams> zAlignX) {
            super(zAlignX, AxisParams.Z, List.of(AxisParams.X));
        }
    }

    public static class ZAlignY extends POCAlignLayoutBuilderParams {
        public List<AlignParams> zAlignY;
        @ConstructorProperties("zAlignY")
        public ZAlignY(List<AlignParams> zAlignY) {
            super(zAlignY, AxisParams.Z, List.of(AxisParams.Y));
        }
    }

    public static class ZAlignXY extends POCAlignLayoutBuilderParams {
        public List<AlignParams> zAlignXY;
        @ConstructorProperties("zAlignXY")
        public ZAlignXY(List<AlignParams> zAlignXY) {
            super(zAlignXY, AxisParams.Z, List.of(AxisParams.X, AxisParams.Y));
        }
    }
}
