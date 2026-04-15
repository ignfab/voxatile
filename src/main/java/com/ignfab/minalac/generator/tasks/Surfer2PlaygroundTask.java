package com.ignfab.minalac.generator.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.tasks.playground.PSLG;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThinLinearVoxelizer2d;

public class Surfer2PlaygroundTask implements TileTask {
    private String polygonName;
    private Placeable surfaceVoxel;
    private ReadableHeightmapSpec atSurface;
    public Placeable lineVoxel;
    public ReadableHeightmapSpec atLine;

    public Surfer2PlaygroundTask(String polygonName, Placeable surfaceVoxel, ReadableHeightmapSpec atSurface) {
        this.polygonName = polygonName;
        this.surfaceVoxel = surfaceVoxel;
        this.atSurface = atSurface;
    }

    private Shape2dVoxelizer surfaceVoxelizer = new SurfaceVoxelizer2d();
    private Shape2dVoxelizer lineVoxelizer2d = new ThinLinearVoxelizer2d();

    @Override
    public void run(GenerationTile tile) {
        Polygon2d polygon = PolygonStore.TESTING.polygon(polygonName);
        ReadableHeightmap ground = tile.heightmap(atSurface);
        for (Positioned2d voxel : tile.limits().to2d().filterInside(surfaceVoxelizer.voxelize(polygon))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();
            surfaceVoxel.place(tile.voxels(), x, y, ground.get(x, y));
        }

        // Shape2dConvertible convertible = new
        // TODO :
        // Faire interface seg vers shape convertible
        ReadableHeightmap lineHeight = tile.heightmap(atLine);
        PSLG pslg = PSLG.fromLinearRing(polygon.shell);
        List<LineString2d> allLines = pslg.bisectorSegments();
        System.out.println(pslg);
        for (LineString2d line : allLines) {
            for (Positioned2d voxel : tile.limits().to2d().filterInside(lineVoxelizer2d.voxelize(line))) {
                int x = voxel.coords().x();
                int y = voxel.coords().y();
                lineVoxel.place(tile.voxels(), x, y, lineHeight.get(x, y));
            }
        }
    }

    private LineString2d segToshape(Segment2d s) {
        System.out.println(s);
        return LineString2d.fromPoints(s.start(), s.end());
    }

    private List<Segment2d> bisectrice(Polygon2d polygon) {
        Segment2d seg1, seg2;
        List<Segment2d> seg = new ArrayList<>();
        for (Segment2d s : polygon.segments()) {
            seg.add(s);
        }
        seg1 = seg.get(0);
        seg2 = seg.get(1);
        Vector2d a = seg1.direction();
        Vector2d b = seg2.direction();
        Vector2d r = a.add(b);
        Segment2d bisectrice = Utils.createSegment(seg1.start().toVector(), r);
        List<Segment2d> bissec = new ArrayList<>();
        bissec.add(bisectrice);
        return bissec;
    }

    private static class Utils {
        public static Segment2d createSegment(Vector2d start, Vector2d direction) {
            Vector2d end = start.add(direction.multiply(10));
            return new Segment2d(start.round(), end.round());
        }
    }

    private static class PolygonStore {
        private final HashMap<String, Polygon2d> store = new HashMap<>();
        public static PolygonStore TESTING = new PolygonStore();

        private PolygonStore() {
            store.put(
                "test",
                new Polygon2d(LinearRing2d.fromPoints(
                    new WorldCoords2d(15, 47),
                    new WorldCoords2d(80, 50),
                    new WorldCoords2d(89, 34),
                    new WorldCoords2d(32, 36),
                    new WorldCoords2d(34, 11),
                    new WorldCoords2d(5, 12)
                )));
            store.put(
                "one",
                new Polygon2d(LinearRing2d.fromPoints(
                    new WorldCoords2d(10, 45),
                    new WorldCoords2d(30, 45),
                    new WorldCoords2d(40, -4),
                    new WorldCoords2d(0, -4)
                )));
        }

        public Polygon2d polygon(String name) {
            if (!store.containsKey(name))
                throw new IllegalArgumentException(name + " does not exist.");
            return store.get(name);
        }
    }
}
