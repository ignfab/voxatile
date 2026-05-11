//package com.ignfab.minalac.generator.placeables.layouts;
//
//import com.ignfab.minalac.generator.exceptions.UnbuildableException;
//import com.ignfab.minalac.generator.placeables.EmptyStructure;
//import com.ignfab.minalac.generator.placeables.LayoutStructure;
//import com.ignfab.minalac.generator.placeables.Structure;
//import com.ignfab.minalac.generator.utils.axis.Axis;
//import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
//import com.ignfab.minalac.generator.utils.axis.mappers.builders.AxisMapperBuilder;
//import com.ignfab.minalac.generator.utils.axis.mappers.builders.ConstantAxisMapperBuilder;
//import com.ignfab.minalac.generator.utils.axis.mappers.builders.StretcherAxisMapperBuilder;
//import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
//
//public class StretchedLayoutBuilder implements LayoutBuilder {
//    private final AxisMapperBuilder xAxisBuilder;
//    private final AxisMapperBuilder yAxisBuilder;
//    private final AxisMapperBuilder zAxisBuilder;
//
//    public StretchedLayoutBuilder(Structure structure, Integer stretchX, Integer stretchY, Integer stretchZ) throws UnbuildableException {
//        AxisMapperBuilder xBase = new ConstantAxisMapperBuilder(structure.limits().sizeX(), structure.limits().minX());
//        AxisMapperBuilder yBase = new ConstantAxisMapperBuilder(structure.limits().sizeY(), structure.limits().minY());
//        AxisMapperBuilder zBase = new ConstantAxisMapperBuilder(structure.limits().sizeZ(), structure.limits().minZ());
//
//        if (stretchX != null) {
//            checkLimits(structure.limits(), stretchX, Axis.X);
//            xAxisBuilder = new StretcherAxisMapperBuilder(xBase, stretchX, 99, 99);
//        } else
//            xAxisBuilder = xBase;
//
//        if (stretchY != null) {
//            checkLimits(structure.limits(), stretchY, Axis.Y);
//            yAxisBuilder = new StretcherAxisMapperBuilder(yBase, stretchY, 99, 99);
//        } else
//            yAxisBuilder = yBase;
//
//        if (stretchZ != null) {
//            checkLimits(structure.limits(), stretchZ, Axis.Z);
//            zAxisBuilder = new StretcherAxisMapperBuilder(zBase, stretchZ, 99, 99);
//        } else {
//            zAxisBuilder = zBase;
//        }
//    }
//
//    private void checkLimits(WorldBBox3d limits, int coord, Axis axis) {
//        if (coord < limits.min().coord(axis) || limits.max().coord(axis) < coord)
//            throw new UnsupportedOperationException("\"at\" value (%d) is outside structure (%d to %d) for axis %s"
//                .formatted(coord, limits.min().coord(axis), limits.max().coord(axis), axis));
//    }
//
//    @Override
//    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnbuildableException {
//        AxisMapper axisX = xAxisBuilder.build(sizeX);
//        AxisMapper axisY = yAxisBuilder.build(sizeY);
//        AxisMapper axisZ = zAxisBuilder.build(sizeZ);
//
//        if (axisX.intervals().length == 0 || axisY.intervals().length == 0 || axisZ.intervals().length == 0)
//            return EmptyStructure.INSTANCE;
//
//        // xisX.intervals().length should always be 1 (For constant & stretched)
//        Structure[][][] structures = new Structure[1][1][1];
//
//        return new LayoutStructure(structures, axisX, axisY, axisZ);
//    }
//
//    @Override
//    public AxisMapperBuilder xAxis() {
//        return xAxisBuilder;
//    }
//
//    @Override
//    public AxisMapperBuilder yAxis() {
//        return yAxisBuilder;
//    }
//
//    @Override
//    public AxisMapperBuilder zAxis() {
//        return zAxisBuilder;
//    }
//}
