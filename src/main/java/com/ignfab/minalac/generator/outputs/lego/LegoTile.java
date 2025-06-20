package com.ignfab.minalac.generator.outputs.lego;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
    private final Map<WorldCoords3d, LegoBrick> bricks = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

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
        if (!limits().contains(position))
            return;
        updateHeightmaps(position.x(), position.y(), position.z());
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
            int x = (position.x() - offset.x()) * 40 + 20 + brick.lduOffset().x();
            int y = -(position.z() + 1) * 24 + brick.lduOffset().y();
            int z = (position.y() - offset.y()) * 40 + 20 + brick.lduOffset().z();
            double yAngle = -Math.toRadians(brick.rotationAroundY());
            out.append("1 ")
                .append(brick.color())
                .append(' ')
                .append(x)
                .append(' ')
                .append(y)
                .append(' ')
                .append(z)
                .append(' ')
                .append(Math.cos(yAngle))
                .append(" 0 ")
                .append(-Math.sin(yAngle))
                .append(" 0 1 0 ")
                .append(Math.sin(yAngle))
                .append(" 0 ")
                .append(Math.cos(yAngle))
                .append(' ')
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
        return bricks.getOrDefault(new WorldCoords3d(x, y, z), LegoBrick.NO_BRICK);
    }
}
