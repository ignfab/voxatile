package com.ignfab.minalac.generator.modules.luanti;

import java.util.Objects;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code LuantiVoxel} class implements a {@link Placeable} voxel for Luanti.
 * A voxel in Luanti, known as node, consists of three parameters: type, param1, param2.
 * @see <a href="https://github.com/luanti-org/luanti/blob/master/src/mapnode.h#L138">Luanti's MapNode class</a> for more information about the node's parameters
 */
public class LuantiVoxel implements Placeable {

    /**
     * Default voxel used on map initialization.
     */
    public static final LuantiVoxel DEFAULT_VOXEL = new LuantiVoxel("air", (byte) 0, (byte) 0);

    /**
     * The node type string.
     */
    protected String type;
    /**
     * The param1 data of the node.
     * This parameter usually contains information about the node's light intensity.
     */
    protected byte param1;
    /**
     * The param2 data of the node.
     * This parameter usually contains information about the node's spacial orientation.
     */
    protected byte param2;

    /**
     * Constructs a new {@code LuantiVoxel}.
     *
     * @param type the node type string
     * @param param1 the param1 data of the node
     * @param param2 the param2 data of the node
     * @see <a href="https://github.com/luanti-org/luanti/blob/master/src/mapnode.h#L138">Luanti's MapNode class</a> for more information about the node's parameters
     */
    public LuantiVoxel(String type, byte param1, byte param2) {
        this.type = type;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z)  {
        if (tile instanceof LuantiVoxelTile luantiTile) {
            place(luantiTile, x, y, z);
        } else {
            throw new IllegalArgumentException("Voxel does not match voxel tile output format");
        }
    }

    /**
     * Places this voxel on a {@link LuantiVoxelTile} at given position.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    private void place(LuantiVoxelTile tile, int x, int y, int z)  {
        // The y-axis in Luanti corresponds, in our chosen coordinate system, to the z-axis, hence the inversion
        tile.set(x, z, y, this);
    }

    /**
     * {@return the node type string}
     */
    public String getType() {
        return type;
    }

    /**
     * {@return the param1 data of the node}
     * This parameter usually contains information about the node's light intensity.
     */
    public byte getParam1() {
        return param1;
    }

    /**
     * {@return the param2 data of the node}
     * This parameter usually contains information about the node's spacial orientation.
     */
    public byte getParam2() {
        return param2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LuantiVoxel that = (LuantiVoxel) o;
        return param1 == that.param1 && param2 == that.param2 && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, param1, param2);
    }

    @Override
    public WorldBBox3d bbox() {
        return WorldBBox3d.ORIGIN;
    }
}
