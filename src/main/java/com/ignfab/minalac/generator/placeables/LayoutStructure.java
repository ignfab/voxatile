package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.IdentityAxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;

import java.util.List;
import java.util.stream.Collectors;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A placeable structure made of several structures.
 * <p>
 * {@link AxisMapper}s are used to map structure coordinate to substructures and in-substructures coordinates.
 */
public class LayoutStructure implements Structure {
    Structure[][][] structures;
    AxisMapper axisX;
    AxisMapper axisY;
    AxisMapper axisZ;
    WorldBBox3d limits;

    /**
     * Creates a new {@code VirtualStructure}.
     *
     * @param structures Three dimensional array of structures. Array dimentions must correspond to AxisMappers sizes.
     */
    public LayoutStructure(Structure[][][] structures, AxisMapper axisX, AxisMapper axisY, AxisMapper axisZ) {
        this.structures = structures;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
        this.limits = new WorldBBox3d(structures[0][0][0].limits().min(), new WorldSize3d(
            axisX.size(),
            axisY.size(),
            axisZ.size()
        ));
        // TODO : ou alors c'est tacite que 0,0,0 contient le point d'origine
    }

    @Override
    public Placeable get(int x, int y, int z) {
        AxisMapper.Mapped aX = axisX.map(x);
        AxisMapper.Mapped aY = axisY.map(y);
        AxisMapper.Mapped aZ = axisZ.map(z);
        return structures[aX.index()][aY.index()][aZ.index()]
            .get(aX.position(), aY.position(), aZ.position());
    }

    @Override
    public WorldBBox3d limits() {
        return limits;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        for (WorldCoords3d c : limits.bbox())
            get(c.x(), c.y(), c.z()).place(tile, c.x() + x, c.y() + y, c.z() + z);
    }

    public static LayoutStructure concatenate(List<Structure> structures, Axis axis) {
        WorldBBox3d limits = WorldBBox3d.surrounding(structures.stream().map(Structure::limits).collect(Collectors.toList()));

        AxisMapper axisX = axis == Axis.X ?
            new SizesAxisMapper(structures.stream().mapToInt(s -> s.limits().sizeX()).toArray()) :
            new IdentityAxisMapper(limits.sizeX());

        AxisMapper axisY = axis == Axis.Y ?
            new SizesAxisMapper(structures.stream().mapToInt(s -> s.limits().sizeY()).toArray()) :
            new IdentityAxisMapper(limits.sizeY());

        AxisMapper axisZ = axis == Axis.Z ?
            new SizesAxisMapper(structures.stream().mapToInt(s -> s.limits().sizeZ()).toArray()) :
            new IdentityAxisMapper(limits.sizeZ());

        Structure[][][] structArray = new Structure
            [axis == Axis.X ? structures.size() : 1]
            [axis == Axis.Y ? structures.size() : 1]
            [axis == Axis.Z ? structures.size() : 1];

        int index = 0;
        for (Structure structure : structures) {
            structArray
                [axis == Axis.X ? index: 0]
                [axis == Axis.Y ? index: 0]
                [axis == Axis.Z ? index: 0] = structure;
            index++;
        }

        return new LayoutStructure(structArray, axisX, axisY, axisZ);
    }
}
