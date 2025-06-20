package com.ignfab.minalac.generator.outputs.lego;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * Implementation of {@link VoxelTile} for Lego.
 */
public class LegoTile extends VoxelTile {
    private final File destination;
    private final Map<WorldCoords3d, LegoBrick> bricks = new HashMap<>();

    /**
     * Creates a new {@code LegoTile}.
     *
     * @param destination Destination file (must be inside existing "tiles" directory)
     * @param limits Limits of this tile (must be contained in world limits)
     */
    public LegoTile(File destination, WorldBBox3d limits) {
        super(limits);
        this.destination = destination;
    }

    /* package-private */ void setBrick(WorldCoords3d position, LegoBrick brick) {
        if (brick.color() < 0)
            bricks.remove(position);
        else
            bricks.put(position, brick);
    }

    @Override
    public void save() throws MapWriteException {
        StringBuilder out = new StringBuilder();
        WorldCoords3d offset = limits().min();
        bricks.forEach((position, brick) -> {
            // XYZ => X/-Z/Y
            int x = (position.x() - offset.x()) * 40 + 20;
            int y = -(position.z() + 1) * 24;
            int z = (position.y() - offset.y()) * 40 + 20;
            out.append("1 ")
                .append(brick.color())
                .append(' ')
                .append(x)
                .append(' ')
                .append(y)
                .append(' ')
                .append(z)
                .append(" 1 0 0 0 1 0 0 0 1 ")
                .append(brick.ref())
                .append("\r\n");
        });
        try {
            FileHelpers.write(destination, out.toString());
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
    }

    @Override
    public Placeable getVoxel(int x, int y, int z) {
        return bricks.get(new WorldCoords3d(x, y, z));
    }
}
