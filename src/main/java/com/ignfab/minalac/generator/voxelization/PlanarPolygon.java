package com.ignfab.minalac.generator.voxelization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.LinearRing3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Polygon3d;

public class PlanarPolygon {
    private final Polygon3d polygon;
    private final Polygon2d projected;
    private final ProjectedRef ref;
    private final boolean discard;

    public PlanarPolygon(List<Vector3d> shell, List<List<Vector3d>> holes) throws IllegalPolygonException {
        List<Vector3d> points = new ArrayList<>(shell);
        holes.forEach(points::addAll);
        Vector3d[] abc = threeMostDistantPoints(points);
        Vector3d a = abc[0];
        Vector3d b = abc[1];
        Vector3d c = abc[2];
        Vector3d ab = b.subtract(a);
        Vector3d ac = c.subtract(a);
        Vector3d cross = ab.cross(ac);
        ref = new ProjectedRef(a, ab.normalized(), cross.cross(ab).normalized(), 2);
        LinearRing2d shell2d = projectPoints(shell);
        List<LinearRing2d> holes2d = new ArrayList<>(holes.size());
        for (List<Vector3d> hole : holes)
            holes2d.add(projectPoints(hole));
        projected = new Polygon2d(shell2d, holes2d);
        LinearRing3d shell3d = roundPoints(shell);
        List<LinearRing3d> holes3d = new ArrayList<>(holes.size());
        for (List<Vector3d> hole : holes)
            holes3d.add(roundPoints(hole));
        polygon = new Polygon3d(shell3d, holes3d);

        // HACK : try to discard small surfaces to avoid unwanted artefacts
        discard = cross.length() < 10.0;
    }

    private Vector3d[] threeMostDistantPoints(List<Vector3d> points) throws IllegalPolygonException {
        double maxArea = 0;
        Vector3d[] result = new Vector3d[3];
        for (int i = 0; i < points.size(); i++) {
            Vector3d a = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Vector3d b = points.get(j);
                Vector3d ab = b.subtract(a);
                for (int k = j + 1; k < points.size(); k++) {
                    Vector3d c = points.get(k);
                    double area = ab.cross(c.subtract(a)).length();
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

    private LinearRing2d projectPoints(List<Vector3d> points) {
        List<WorldCoords2d> points2d = new ArrayList<>();
        for (int i = 0; i < points.size(); i++)
            points2d.add(ref.project(points.get(i)));
        return LinearRing2d.fromPoints(points2d);
    }

    private LinearRing3d roundPoints(List<Vector3d> points) {
        List<WorldCoords3d> points3d = new ArrayList<>();
        for (int i = 0; i < points.size(); i++)
            points3d.add(points.get(i).round());
        return LinearRing3d.fromPoints(points3d);
    }

    public Iterable<Positioned3d> iterable() {
        if (discard)
            return Collections.emptyList();
        else
            return () -> Iterators.remap(new Polygon2dIterator(projected, true), c -> ref.revert((WorldCoords2d) c));
    }

    public record ProjectedRef(Vector3d origin, Vector3d i, Vector3d j, double scale) {
        public WorldCoords2d project(Vector3d pos) {
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

    public static class IllegalPolygonException extends Exception {
        public IllegalPolygonException(String message) {
            super(message);
        }
    }
}
