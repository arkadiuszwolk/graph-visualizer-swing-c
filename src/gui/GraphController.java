package gui;

import models.Graph;
import models.Vertex;
import java.awt.event.*;
import java.awt.Point;

public class GraphController extends MouseAdapter {
    private final Graph graph;
    private final GraphState state;
    private final GraphPanel panel;
    private final InfoPanel infoPanel;

    public GraphController(Graph graph, GraphState state, GraphPanel panel, InfoPanel infoPanel) {
        this.graph = graph;
        this.state = state;
        this.panel = panel;
        this.infoPanel = infoPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        state.lastMousePosition = e.getPoint();
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

        if (state.isDraggingVertex && state.selectedVertex != null) {
            double worldX = (currentPoint.x - state.offsetX) / state.zoomFactor;
            double worldY = (currentPoint.y - state.offsetY) / state.zoomFactor;
            state.selectedVertex.setX(worldX);
            state.selectedVertex.setY(worldY);
        } else {
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
        if (found != state.hoveredVertex) {
            state.hoveredVertex = found;
            panel.repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double oldZoom = state.zoomFactor;
        double zoomAmount = 0.1;

        if (e.getWheelRotation() < 0) {
            state.zoomFactor += zoomAmount;
        } else {
            state.zoomFactor = Math.max(0.1, state.zoomFactor - zoomAmount);
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
}