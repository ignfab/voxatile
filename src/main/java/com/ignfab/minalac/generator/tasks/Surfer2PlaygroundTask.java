package com.ignfab.minalac.generator.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.tasks.playground.PSLG;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
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


        ReadableHeightmap lineHeight = tile.heightmap(atLine);
        /*
        PSLG pslg = PSLG.fromLinearRing(polygon.shell);
        List<LineString2d> allLines = pslg.bisectorLineStrings();
        for (LineString2d line : allLines) {
            for (Positioned2d voxel : tile.limits().to2d().filterInside(lineVoxelizer2d.voxelize(line))) {
                int x = voxel.coords().x();
                int y = voxel.coords().y();
                lineVoxel.place(tile.voxels(), x, y, lineHeight.get(x, y));
            }
        }*/
        List<LineString2d> skeleton = PolygonStore.TESTING.skeleton(polygonName);
        List<Segment2d> edgeSegments = polygon.tmp_segmentsAsList();
        List<Segment2d> skeletonSegments = lineStringToSegment(skeleton);
        for (Positioned2d voxel : tile.limits().to2d().filterInside(surfaceVoxelizer.voxelize(polygon))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            double localMax = localMaximum(x, y, edgeSegments, skeletonSegments); // TODO
            double distanceToSkeleton = minDistance(x, y, skeletonSegments);
            double height = localMax - distanceToSkeleton;
            height = distanceToSkeleton;

            int altitude = (int) height + lineHeight.get(x, y);
            lineVoxel.place(tile.voxels(), x, y, altitude);
        }

    }

    private static double minDistance(int x, int y, List<Segment2d> segments) {
        double distance = Double.MAX_VALUE;
        for (Segment2d segment : segments) {
            // double d = Math.abs(segment.signedDistanceTo(x, y));
            double d = segment.tmp_distance(x, y);
            if (distance > d)
                distance = d;
        }
        return distance;
    }

    private static double localMaximum(int x, int y, List<Segment2d> edges, List<Segment2d> skeletons) {
        // Bourrin

        // Trouver segment du squelette le plus proche
        double distance = Double.MAX_VALUE;
        Segment2d closestSkeleton = null;
        for (Segment2d segment : skeletons) {
            double d = segment.tmp_distance(x, y);
            // double d = Math.abs(segment.signedDistanceTo(x, y));
            if (distance > d) {
                distance = d;
                closestSkeleton = segment;
            }
        }

        double t = closestSkeleton.nearestPointIndex(x, y) / closestSkeleton.length();
        WorldCoords2d a = new Vector2d(
            closestSkeleton.start().x() + t * closestSkeleton.direction().x(),
            closestSkeleton.start().y() + t * closestSkeleton.direction().y()
        ).round();

        return minDistance(a.x(), a.x(), edges);
    }

    private static List<Segment2d> lineStringToSegment(List<LineString2d> lineStrings) {
        List<Segment2d> segments = new ArrayList<>();
        for (LineString2d l : lineStrings)
            for (Segment2d s : l.segments())
                segments.add(s);
        return segments;
    }

    private static class Utils {
        public static Segment2d createSegment(Vector2d start, Vector2d direction) {
            Vector2d end = start.add(direction.multiply(10));
            return new Segment2d(start.round(), end.round());
        }
    }

    private static class PolygonStore {
        private final HashMap<String, Polygon2d> store = new HashMap<>();
        private final HashMap<String, List<LineString2d>> skeleton = new HashMap<>();
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
            ArrayList<LineString2d> oneSkeleton = new ArrayList<>();
            oneSkeleton.add(LineString2d.fromPoints(
                new WorldCoords2d(10, 45),
                new WorldCoords2d(20, 33)
            ));
            oneSkeleton.add(LineString2d.fromPoints(
                new WorldCoords2d(30, 45),
                new WorldCoords2d(20, 33)
            ));
            oneSkeleton.add(LineString2d.fromPoints(
                new WorldCoords2d(20, 33),
                new WorldCoords2d(20, 12)
            ));
            oneSkeleton.add(LineString2d.fromPoints(
                new WorldCoords2d(0, -4),
                new WorldCoords2d(20, 12)
            ));
            oneSkeleton.add(LineString2d.fromPoints(
                new WorldCoords2d(40, -4),
                new WorldCoords2d(20, 12)
            ));
            skeleton.put(
                "one",
                oneSkeleton
            );
        }

        public Polygon2d polygon(String name) {
            if (!store.containsKey(name))
                throw new IllegalArgumentException(name + " does not exist.");
            return store.get(name);
        }

        public List<LineString2d> skeleton(String name) {
            if (!skeleton.containsKey(name))
                throw new IllegalArgumentException(name + " does not exist.");
            return skeleton.get(name);
        }

        public static void main(String[] args) {
            Segment2d s = new Segment2d(new WorldCoords2d(20, 33),
                new WorldCoords2d(20, 12));
            double a = s.tmp_distance(20, 0);
            System.out.println(a);
        }
    }
}
