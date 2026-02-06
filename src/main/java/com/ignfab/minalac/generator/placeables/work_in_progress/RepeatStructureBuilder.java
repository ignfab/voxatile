package com.ignfab.minalac.generator.placeables.work_in_progress;

public class RepeatStructureBuilder extends ResizedStructureBuilderImpl {
    private RepeatStructureBuilder(ResizedStructureBuilder structureBuilder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        super(structureBuilder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public static ResizedStructureBuilder X(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder Y(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder Z(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder XY(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())

        );
    }
}
