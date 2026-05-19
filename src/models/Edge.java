package models;

import java.util.Objects;

public class Edge {
    private final String label;
    private final Vertex start;
    private final Vertex end;
    private final double weight;

    // Konstruktor

    public Edge(String label, Vertex start, Vertex end, double weight) {
        this.label = label;
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    // Gettery i Settery

    public String getLabel() {
        return label;
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }

    // Przesłanianie metod

    @Override
    public String toString() {
        return "Edge{" +
                "label='" + label + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge edge)) return false;
        return Objects.equals(label, edge.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }
}
