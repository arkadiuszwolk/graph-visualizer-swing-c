package models;

import java.util.Objects;

public class Vertex {
    private final String id;
    private double x;
    private double y;

    // Konstruktor

    public Vertex(String id) {
        this.id = id;
        this.x = 0;
        this.y = 0;
    }

    // Gettery i Settery

    public String getId() { return id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    // Przesłanianie metod

    @Override
    public String toString() {
        return "Vertex{id: " + id + ", x: " + x + ", y: " + y + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;

        Vertex other = (Vertex) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
