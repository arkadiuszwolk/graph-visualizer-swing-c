package gui;

import models.Graph;
import models.Vertex;
import java.awt.event.*;
import java.awt.Point;

public class GraphController extends MouseAdapter {
    private final Graph graph;
    private final GraphState state;
    private final GraphPanel panel;

    public GraphController(Graph graph, GraphState state, GraphPanel panel) {
        this.graph = graph;
        this.state = state;
        this.panel = panel;
    }

    // --- KLIKNIĘCIE ---
    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        state.lastMousePosition = p;

        // Przeliczamy kliknięcie na współrzędne grafu (World Coordinates)
        double worldX = (p.x - state.offsetX) / state.zoomFactor;
        double worldY = (p.y - state.offsetY) / state.zoomFactor;

        // Sprawdzamy, czy trafiliśmy w wierzchołek
        state.selectedVertex = findVertexAt(worldX, worldY);

        if (state.selectedVertex != null) {
            state.isDraggingVertex = true;
        }

        panel.repaint();
    }

    // --- PRZECIĄGANIE ---
    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();

        if (state.isDraggingVertex && state.selectedVertex != null) {
            // Przesuwamy wierzchołek
            state.selectedVertex.setX((p.x - state.offsetX) / state.zoomFactor);
            state.selectedVertex.setY((p.y - state.offsetY) / state.zoomFactor);
        } else {
            // Przesuwamy całą kamerę (Pan)
            if (state.lastMousePosition != null) {
                state.offsetX += p.x - state.lastMousePosition.x;
                state.offsetY += p.y - state.lastMousePosition.y;
                state.lastMousePosition = p;
            }
        }
        panel.repaint();
    }

    // --- RUCH MYSZKI (Hover) ---
    @Override
    public void mouseMoved(MouseEvent e) {
        double worldX = (e.getX() - state.offsetX) / state.zoomFactor;
        double worldY = (e.getY() - state.offsetY) / state.zoomFactor;

        Vertex found = findVertexAt(worldX, worldY);

        if (found != state.hoveredVertex) {
            state.hoveredVertex = found;
            panel.repaint();
        }
    }

    // --- ZOOM (Kółko myszy) ---
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double oldZoom = state.zoomFactor;
        double zoomAmount = 0.1;

        if (e.getWheelRotation() < 0) state.zoomFactor += zoomAmount;
        else state.zoomFactor = Math.max(0.1, state.zoomFactor - zoomAmount);

        // Korekta offsetu, aby zoom był do punktu myszy
        double mouseX = e.getX();
        double mouseY = e.getY();
        double worldX = (mouseX - state.offsetX) / oldZoom;
        double worldY = (mouseY - state.offsetY) / oldZoom;

        state.offsetX = mouseX - worldX * state.zoomFactor;
        state.offsetY = mouseY * 1 - worldY * state.zoomFactor;

        panel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        state.isDraggingVertex = false;
        state.lastMousePosition = null;
    }

    // Metoda pomocnicza do szukania wierzchołka pod kursorem
    private Vertex findVertexAt(double x, double y) {
        double threshold = 15; // promień klikalności
        for (Vertex v : graph.getVertices()) {
            double dx = v.getX() - x;
            double dy = v.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < threshold) return v;
        }
        return null;
    }
}