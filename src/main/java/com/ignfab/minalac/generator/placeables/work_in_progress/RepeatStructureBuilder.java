package com.ignfab.minalac.generator.placeables.work_in_progress;

public class RepeatStructureBuilder extends ResizedStructureBuilderImpl {
    private RepeatStructureBuilder(ResizedStructureBuilder structureBuilder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        super(structureBuilder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public static ResizedStructureBuilder X(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().ask(0)),
            new IndexMapperBuilder.Identity(builder.axisY().ask(0)),
            new IndexMapperBuilder.Identity(builder.axisZ().ask(0))
        );
    }

    public static ResizedStructureBuilder Y(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().ask(0)),
            new IndexMapperBuilder.Equalizer(builder.axisY().ask(0)),
            new IndexMapperBuilder.Identity(builder.axisZ().ask(0))
        );
    }

    public static ResizedStructureBuilder Z(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().ask(0)),
            new IndexMapperBuilder.Identity(builder.axisY().ask(0)),
            new IndexMapperBuilder.Equalizer(builder.axisZ().ask(0))
        );
    }

    public static ResizedStructureBuilder XY(ResizedStructureBuilder builder) {
        return new RepeatStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().ask(0)),
            new IndexMapperBuilder.Equalizer(builder.axisY().ask(0)),
            new IndexMapperBuilder.Identity(builder.axisZ().ask(0))

        );
    }
}
