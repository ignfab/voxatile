package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.BuildingVoxelizer;
import com.ignfab.minalac.generator.voxelization.PlanarPolygon;

import java.util.List;

public class CityBuildingModel extends Model implements BuildingVoxelizable {
    private final List<PlanarPolygon> ground;
    private final List<PlanarPolygon> walls;
    private final List<PlanarPolygon> roof;
    private final WorldCoords3d center;

    public CityBuildingModel(List<PlanarPolygon> ground, List<PlanarPolygon> walls, List<PlanarPolygon> roof, WorldCoords3d center) {
        this.ground = ground;
        this.walls = walls;
        this.roof = roof;
        this.center = center;
    }

    @Override
    public String salt() {
        return "%d/%d/%d".formatted(center.x(), center.y(), center.z());
    }

    @Override
    public BuildingVoxelizer voxelize3d(WorldBBox3d bbox) {
        BuildingVoxelizer voxelizer = new BuildingVoxelizer(bbox);
        voxelizer.addAll(BuildingSurfaceType.GROUND, ground);
        voxelizer.addAll(BuildingSurfaceType.WALL, walls);
        voxelizer.addAll(BuildingSurfaceType.ROOF, roof);
        return voxelizer;
    }
}
