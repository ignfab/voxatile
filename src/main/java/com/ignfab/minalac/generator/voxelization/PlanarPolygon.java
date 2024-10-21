package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polyline2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polygon3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polyline3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

import java.util.ArrayList;
import java.util.List;

public class PlanarPolygon implements Shape3d {
    private final Polygon3d polygon;
    private final Polygon2d projected;
    private final ProjectedRef ref;

    public PlanarPolygon(List<Coords3d> shell, List<List<Coords3d>> holes) throws IllegalPolygonException {
        List<Coords3d> points = new ArrayList<>(shell);
        holes.forEach(points::addAll);
        Coords3d[] abc = threeMostDistantPoints(points);
        Coords3d a = abc[0];
        Coords3d b = abc[1];
        Coords3d c = abc[2];
        Vector3d ab = new Vector3d(a, b);
        Vector3d ac = new Vector3d(a, c);
        Vector3d cross = ab.cross(ac);
        ref = new ProjectedRef(a, ab.normalized(), cross.cross(ab).normalized(), 2);
        Polyline2d shell2d = projectPoints(shell);
        List<Polyline2d> holes2d = new ArrayList<>(holes.size());
        for (List<Coords3d> hole : holes)
            holes2d.add(projectPoints(hole));
        projected = new Polygon2d(shell2d, holes2d);
        Polyline3d shell3d = roundPoints(shell);
        List<Polyline3d> holes3d = new ArrayList<>(holes.size());
        for (List<Coords3d> hole : holes)
            holes3d.add(roundPoints(hole));
        polygon = new Polygon3d(shell3d, holes3d);
    }

    private Coords3d[] threeMostDistantPoints(List<Coords3d> points) throws IllegalPolygonException {
        double maxArea = 0;
        Coords3d[] result = new Coords3d[3];
        for (int i = 0; i < points.size(); i++) {
            Coords3d a = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Coords3d b = points.get(j);
                Vector3d ab = new Vector3d(a, b);
                for (int k = j + 1; k < points.size(); k++) {
                    Coords3d c = points.get(k);
                    double area = ab.cross(new Vector3d(a, c)).length() / 2;
                    if (area > maxArea) {
                        maxArea = area;
                        result[0] = points.get(i);
                        result[1] = points.get(j);
                        result[2] = points.get(k);
                    }
                }
            }
        }
        if (maxArea == 0)
            throw new IllegalPolygonException("Polygon must have a least 3 distinct points");
        return result;
    }

    private Polyline2d projectPoints(List<Coords3d> points) {
        List<Line2d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line2d(ref.project(points.get(i - 1)), ref.project(points.get(i))));
        return new Polyline2d(lines);
    }

    private Polyline3d roundPoints(List<Coords3d> points) {
        List<Line3d> lines = new ArrayList<>();
        for (int i = 1; i < points.size(); i++)
            lines.add(new Line3d(points.get(i - 1).round(), points.get(i).round()));
        return new Polyline3d(lines);
    }

    public Iterable<Positioned3d> iterable(boolean includeBorders) {
        return () -> Iterators.remap(new Polygon2dIterator(projected, includeBorders), c -> ref.revert((WorldCoords2d) c));
    }

    @Override
    public Iterable<Positioned3d> allVoxels() {
        return iterable(true);
    }

    @Override
    public Iterable<Positioned3d> insideVoxels() {
        return iterable(false);
    }

    @Override
    public Iterable<LineVoxel3d> borderVoxels() {
        return polygon.borderVoxels();
    }

    public record ProjectedRef(Coords3d origin, Vector3d i, Vector3d j, double scale) {
        public WorldCoords2d project(Coords3d pos) {
            double x = pos.x() - origin.x();
            double y = pos.y() - origin.y();
            double z = pos.z() - origin.z();
            return WorldCoords2d.round(i.dot(x, y, z) * scale, j.dot(x, y, z) * scale);
        }

        public WorldCoords3d revert(WorldCoords2d pos) {
            double x = pos.x() / scale;
            double y = pos.y() / scale;
            return WorldCoords3d.round(
                origin.x() + i.x() * x + j.x() * y,
                origin.y() + i.y() * x + j.y() * y,
                origin.z() + i.z() * x + j.z() * y
            );
        }
    }

    public record Coords3d(double x, double y, double z) {
        public Coords3d(MapCoordinates mapCoords, double z) {
            this(mapCoords.x(), mapCoords.y(), z);
        }

        public WorldCoords3d round() {
            return WorldCoords3d.round(x, y, z);
        }
    }

    public record Vector3d(double x, double y, double z) {
        public Vector3d(Coords3d a, Coords3d b) {
            this(b.x() - a.x(), b.y() - a.y(), b.z() - a.z());
        }

        public double dot(Vector3d v) {
            return dot(v.x, v.y, v.z);
        }

        public double dot(double x, double y, double z) {
            return this.x * x + this.y * y + this.z * z;
        }

        public Vector3d cross(Vector3d v) {
            return new Vector3d(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
            );
        }

        public Vector3d normalized() {
            if (isZero())
                return this;
            double length = length();
            return new Vector3d(x / length, y / length, z / length);
        }

        public double length() {
            return Math.sqrt(dot(this));
        }

        public boolean isZero() {
            return x == 0 && y == 0 && z == 0;
        }
    }

    public static class IllegalPolygonException extends Exception {
        public IllegalPolygonException(String message) {
            super(message);
        }
    }
}
