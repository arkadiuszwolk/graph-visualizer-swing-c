package gui;

import io.CoordinatesReader;
import models.Graph;
import models.Vertex;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;

public class Controller extends MouseAdapter {
    private final Graph graph;
    private final State state;
    private final GraphPanel panel;
    private final InfoPanel infoPanel;

    public Controller(Graph graph, State state, GraphPanel panel, InfoPanel infoPanel) {
        this.graph = graph;
        this.state = state;
        this.panel = panel;
        this.infoPanel = infoPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        state.lastMousePosition = e.getPoint();

        // Jeśli kliknięto wierzchołek zapisuje się on w stanie,
        // Jeśli nie trafiono w wierzchołek findVertexAt(...) zwraca null
        // i ustawia go w stanie (efekt odkliknięcia)
        state.selectedVertex = findVertexAt(e.getPoint());

        if (state.selectedVertex != null) {
            state.isDraggingVertex = true;
        }

        panel.repaint();
        infoPanel.updateInfo();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currentPoint = e.getPoint();

        // Gałąź #1 (Kliknięcie wierzchołka i przeciąganie)
        // Przeliczanie położenia kursora (świat pikseli ekranu) na współrzędne grafu (świat współrzędnych grafu)
        // Korzystamy z operacji odwrotnej do tej przy rysowaniu Edge
        // Aktualizacja współrzędnych w instancji grafu
        if (state.isDraggingVertex && state.selectedVertex != null) {
            double worldX = (currentPoint.x - state.offsetX) / state.zoomFactor;
            double worldY = (currentPoint.y - state.offsetY) / state.zoomFactor;
            state.selectedVertex.setX(worldX);
            state.selectedVertex.setY(worldY);
        } else {
            // Gałąź #2 (Kliknięcie poza wierzchołkiem i przeciąganie)
            // Zwykła zmiana offsetu w state
            if (state.lastMousePosition != null) {
                state.offsetX += currentPoint.x - state.lastMousePosition.x;
                state.offsetY += currentPoint.y - state.lastMousePosition.y;
                state.lastMousePosition = currentPoint;
            }
        }
        panel.repaint();
        infoPanel.updateInfo();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Vertex found = findVertexAt(e.getPoint());

        // Zmiana stanu wierzchołka nad którym jest kursor na :hovered
        if (found != state.hoveredVertex) {
            state.hoveredVertex = found;
            panel.repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double oldZoom = state.zoomFactor;
        double zoomAmount = 1.1;

        if (e.getWheelRotation() < 0) {
            state.zoomFactor *= zoomAmount;
        } else {
            state.zoomFactor = Math.max(0.1, state.zoomFactor / zoomAmount);
        }

        Point mousePoint = e.getPoint();
        double worldX = (mousePoint.x - state.offsetX) / oldZoom;
        double worldY = (mousePoint.y - state.offsetY) / oldZoom;

        state.offsetX = mousePoint.x - worldX * state.zoomFactor;
        state.offsetY = mousePoint.y - worldY * state.zoomFactor;

        panel.repaint();
        infoPanel.updateInfo();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        state.isDraggingVertex = false;
    }

    private Vertex findVertexAt(Point p) {
        if (p == null) return null;
        double screenThreshold = 15;
        for (Vertex v : graph.getVertices()) {
            double vScreenX = v.getX() * state.zoomFactor + state.offsetX;
            double vScreenY = v.getY() * state.zoomFactor + state.offsetY;
            double dx = vScreenX - p.x;
            double dy = vScreenY - p.y;
            if (Math.sqrt(dx * dx + dy * dy) < screenThreshold) {
                return v;
            }
        }
        return null;
    }

    public void zoomAroundCenter(double multiplier) {
        double oldZoom = state.zoomFactor;
        state.zoomFactor *= multiplier;

        if (state.zoomFactor < 0.1) {
            state.zoomFactor = 0.1;
            return;
        }

        double centerX = panel.getWidth() / 2.0;
        double centerY = panel.getHeight() / 2.0;

        double worldX = (centerX - state.offsetX) / oldZoom;
        double worldY = (centerY - state.offsetY) / oldZoom;

        state.offsetX = centerX - worldX * state.zoomFactor;
        state.offsetY = centerY - worldY * state.zoomFactor;

        panel.repaint();
        infoPanel.updateInfo();
    }

    public void refreshFromFile() {
        try {
            CoordinatesReader coordsReader = new CoordinatesReader();
            coordsReader.updateCoordinates("src/data/coords.txt", this.graph);

            panel.centerView();
            panel.repaint();

            System.out.println("Pomyślnie zresetowano pozycje z pliku.");
        } catch (java.io.FileNotFoundException e) {
            JOptionPane.showMessageDialog(panel, "Nie znaleziono pliku coords.txt!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}