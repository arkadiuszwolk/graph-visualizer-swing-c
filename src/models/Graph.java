package models;

import java.util.*;

public class Graph {
    private final Map<String, Vertex> vertices = new HashMap<>();
    private final Set<Edge> edges = new HashSet<>();

    public void addEdge(String label, String startId, String endId, double weight) {
        vertices.putIfAbsent(startId, new Vertex(startId));
        vertices.putIfAbsent(endId, new Vertex(endId));

        Vertex vStart = vertices.get(startId);
        Vertex vEnd = vertices.get(endId);

        edges.add(new Edge(label, vStart, vEnd, weight));
    }

    public Vertex getVertex(String id) {
        return vertices.get(id);
    }

    public Collection<Vertex> getVertices() {
        return vertices.values();
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void clear() {
        vertices.clear();
        edges.clear();
    }
}
