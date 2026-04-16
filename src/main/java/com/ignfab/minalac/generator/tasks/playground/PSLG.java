package com.ignfab.minalac.generator.tasks.playground;

import java.util.ArrayList;
import java.util.List;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;

public class PSLG {
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    private PSLG(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public static PSLG fromLinearRing(LinearRing2d r) {
        List<Vertex> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        LinearRing2d ring = r.toClockwise();
        for (Point2d point : ring.points()) {
            vertices.add(new Vertex(point.coords().toVector()));
        }

        int n = vertices.size();
        for (int i = 0; i < n ; i++ ) {
            // TODO : à verifier
            Edge e = new Edge(vertices.get(i), vertices.get((i + 1) % n));
            edges.add(e);
        }
        return new PSLG(vertices, edges);
    }

    private void computeBisector() {
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Vector2d current = edges.get(i).direction;
            Vector2d previous = edges.get(Math.floorMod(i - 1, n)).direction;
            vertices.get(i).bisector = previous.opposite().add(current);
        }
    }

    public List<LineString2d> bisectorLineStrings() {
        List<LineString2d> lines = new ArrayList<>();
        computeBisector();
        System.out.println(vertices);
        for (Vertex v : vertices) {
            Vector2d currentBisector = v.bisector;
            if (currentBisector != null) {
                Vector2d start = v.position.add(currentBisector.multiply(-200));
                Vector2d end = v.position.add(currentBisector.multiply(200));
                lines.add(
                    LineString2d.fromPoints(start.round(), end.round())
                );
            }
        }
        return lines;
    }

    public List<LineString2d> edgeLineStrings() {
        List<LineString2d> lines = new ArrayList<>();
        for (Edge edge : edges) {
            LineString2d current = LineString2d.fromPoints(edge.start.position.round(), edge.end.position.round());
            lines.add(current);
        }
        return lines;
    }

    public static class Vertex {
        public Vector2d position;
        // A voir si à stocker ici
        public Vector2d bisector;

        public Vertex(Vector2d position) {
            this.position = position;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Vertex{");
            sb.append("position=").append(position);
            sb.append(", bisector=").append(bisector);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Edge {
        private Vertex start;
        private Vertex end;
        private Vector2d direction;
        private Vector2d insideNormal;
        private double weight = 1.0;

        public Edge(Vertex start, Vertex end) {
            this.start = start;
            this.end = end;
            // TODO à vérifier et normaliser et cas paricuiler
            direction = end.position.subtract(start.position).normalize();
            insideNormal = direction.normal();
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Edge{");
            sb.append("start=").append(start);
            sb.append(", end=").append(end);
            sb.append(", direction=").append(direction);
            sb.append(", insideNormal=").append(insideNormal);
            sb.append(", weight=").append(weight);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PSLG{");
        sb.append("vertices=").append(vertices);
        sb.append(", edges=").append(edges);
        sb.append('}');
        return sb.toString();
    }
}
