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

    // --- KLIKNIĘCIE MYSZKI ---
    @Override
    public void mousePressed(MouseEvent e) {
        // Zapisujemy aktualną pozycję do przesuwania kamery (Pan)
        state.lastMousePosition = e.getPoint();

        // Szukamy wierzchołka pod kursoram używając współrzędnych ekranowych
        state.selectedVertex = findVertexAt(e.getPoint());

        if (state.selectedVertex != null) {
            state.isDraggingVertex = true;
        }

        panel.repaint();
    }

    // --- PRZECIĄGANIE MYSZKI ---
    @Override
    public void mouseDragged(MouseEvent e) {
        Point currentPoint = e.getPoint();

        if (state.isDraggingVertex && state.selectedVertex != null) {
            // Przesuwamy wierzchołek - przeliczamy nową pozycję ekranową na współrzędne ŚWIATA
            double worldX = (currentPoint.x - state.offsetX) / state.zoomFactor;
            double worldY = (currentPoint.y - state.offsetY) / state.zoomFactor;

            state.selectedVertex.setX(worldX);
            state.selectedVertex.setY(worldY);
        } else {
            // Przesuwamy całą kamerę (Pan)
            if (state.lastMousePosition != null) {
                state.offsetX += currentPoint.x - state.lastMousePosition.x;
                state.offsetY += currentPoint.y - state.lastMousePosition.y;
                state.lastMousePosition = currentPoint;
            }
        }
        panel.repaint();
    }

    // --- RUCH MYSZKI (Hover) ---
    @Override
    public void mouseMoved(MouseEvent e) {
        // Sprawdzamy co jest pod myszką przy każdym ruchu
        Vertex found = findVertexAt(e.getPoint());

        if (found != state.hoveredVertex) {
            state.hoveredVertex = found;
            panel.repaint(); // Odświeżamy, by pokazać/ukryć szarą obwódkę
        }
    }

    // --- ZOOM (Kółko myszy) ---
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double oldZoom = state.zoomFactor;
        double zoomAmount = 0.1;

        // Obliczamy nowy zoom
        if (e.getWheelRotation() < 0) {
            state.zoomFactor += zoomAmount;
        } else {
            state.zoomFactor = Math.max(0.1, state.zoomFactor - zoomAmount);
        }

        // Korekta offsetu (Zoom do punktu myszy)
        Point mousePoint = e.getPoint();
        double worldX = (mousePoint.x - state.offsetX) / oldZoom;
        double worldY = (mousePoint.y - state.offsetY) / oldZoom;

        state.offsetX = mousePoint.x - worldX * state.zoomFactor;
        state.offsetY = mousePoint.y - worldY * state.zoomFactor;

        panel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        state.isDraggingVertex = false;
        // Ważne: nie czyścimy lastMousePosition tutaj,
        // bo mousePressed nadpisze go przy następnym kliku
    }

    // --- METODY POMOCNICZE ---

    /**
     * Szuka wierzchołka sprawdzając odległość na EKRANIE (piksele).
     * Dzięki temu klikalność jest niezależna od poziomu zoomu.
     */
    private Vertex findVertexAt(Point p) {
        if (p == null) return null;

        double screenThreshold = 15; // Promień klikalności w pikselach

        for (Vertex v : graph.getVertices()) {
            // Przeliczamy pozycję wierzchołka z modelu na aktualną pozycję na ekranie
            double vScreenX = v.getX() * state.zoomFactor + state.offsetX;
            double vScreenY = v.getY() * state.zoomFactor + state.offsetY;

            // Obliczamy odległość euklidesową na ekranie
            double dx = vScreenX - p.x;
            double dy = vScreenY - p.y;

            if (Math.sqrt(dx * dx + dy * dy) < screenThreshold) {
                return v;
            }
        }
        return null;
    }

    /**
     * Metoda dla przycisków Toolbaru (Zoom + / -).
     * Wykonuje zoom względem geometrycznego środka panelu.
     */
    public void zoomAroundCenter(double multiplier) {
        double oldZoom = state.zoomFactor;
        state.zoomFactor *= multiplier;

        if (state.zoomFactor < 0.1) {
            state.zoomFactor = 0.1;
            return;
        }

        // Punkt odniesienia: środek widocznego panelu
        double centerX = panel.getWidth() / 2.0;
        double centerY = panel.getHeight() / 2.0;

        // Gdzie w "świecie" jest teraz środek ekranu?
        double worldX = (centerX - state.offsetX) / oldZoom;
        double worldY = (centerY - state.offsetY) / oldZoom;

        // Ustawiamy nowy offset tak, by ten sam punkt świata pozostał na środku
        state.offsetX = centerX - worldX * state.zoomFactor;
        state.offsetY = centerY - worldY * state.zoomFactor;

        panel.repaint();
    }
}