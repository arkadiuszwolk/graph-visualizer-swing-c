package gui;

import models.Edge;
import models.Graph;
import models.Vertex;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {
    private final Graph graph;
    private final State state;

    public GraphPanel(Graph graph, State state) {
        this.graph = graph;
        this.state = state;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rysowanie krawędzi
        for (Edge edge : graph.getEdges()) {
            drawEdge(g2d, edge);
        }

        // Rysowanie wierzchołków
        for (Vertex vertex : graph.getVertices()) {
            drawVertex(g2d, vertex);
        }
    }

    private void drawEdge(Graphics2D g2d, Edge edge) {
        // Rysowanie krawędzi
        int x1 = (int) (edge.getStart().getX() * state.zoomFactor + state.offsetX);
        int y1 = (int) (edge.getStart().getY() * state.zoomFactor + state.offsetY);
        int x2 = (int) (edge.getEnd().getX() * state.zoomFactor + state.offsetX);
        int y2 = (int) (edge.getEnd().getY() * state.zoomFactor + state.offsetY);

        if (edge.getStart() == state.selectedVertex || edge.getEnd() == state.selectedVertex) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
        } else {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1));
        }
        g2d.drawLine(x1, y1, x2, y2);

        // Położenie i tekst etykiety wagi
        String weightText = String.valueOf(edge.getWeight());
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;

        // Prostokąt etykiety wagi
        g2d.setFont(new Font("Arial", Font.ITALIC, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(weightText);
        int textHeight = fm.getAscent();
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(midX - textWidth / 2 - 2, midY - textHeight / 2 - 2, textWidth + 4, textHeight + 2);

        // Rysowanie tekstu etykiety
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(weightText, midX - textWidth / 2, midY + textHeight / 2);
    }

    private void drawVertex(Graphics2D g2d, Vertex v) {
        int r = 10;
        int x = (int) (v.getX() * state.zoomFactor + state.offsetX);
        int y = (int) (v.getY() * state.zoomFactor + state.offsetY);

        // Efekt wierzchołka dla stanu :hovered
        if (v == state.hoveredVertex) {
            g2d.setColor(new Color(200, 200, 200, 150));
            g2d.fillOval(x - r - 5, y - r - 5, (r + 5) * 2, (r + 5) * 2);
        }

        // Efekty wierzchołka dla stanu neutralnego i :selected
        g2d.setColor(v == state.selectedVertex ? Color.ORANGE : Color.BLUE);
        g2d.fillOval(x - r, y - r, r * 2, r * 2);

        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - r, y - r, r * 2, r * 2);

        g2d.drawString(v.getId(), x + r + 2, y);
    }

    public void centerView() {
        if (graph.getVertices().isEmpty()) return;

        // Szukanie skrajnych współrzędnych (Bounding Box)
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Vertex v : graph.getVertices()) {
            if (v.getX() < minX) minX = v.getX();
            if (v.getX() > maxX) maxX = v.getX();
            if (v.getY() < minY) minY = v.getY();
            if (v.getY() > maxY) maxY = v.getY();
        }

        // Rozmiary Bounding Box
        double graphWidth = maxX - minX;
        double graphHeight = maxY - minY;

        // Zabezpieczenie przed dzieleniem przez 0
        if (graphWidth == 0) graphWidth = 1;
        if (graphHeight == 0) graphHeight = 1;

        // Obliczanie skali (z paddingiem 0.7)
        double padding = 0.7;
        double scaleX = getWidth() * padding / graphWidth;
        double scaleY = getHeight() * padding / graphHeight;

        // Wybór mniejszej skali, żeby graf zmieścił się w obu osiach
        state.zoomFactor = Math.min(scaleX, scaleY);

        // Obliczanie przesunięcia (Offset)
        // (Środek grafu ma wylądować na środku panelu)
        double graphCenterX = (minX + maxX) / 2.0;
        double graphCenterY = (minY + maxY) / 2.0;

        state.offsetX = (getWidth() / 2.0) - (graphCenterX * state.zoomFactor);
        state.offsetY = (getHeight() / 2.0) - (graphCenterY * state.zoomFactor);

        repaint();
    }
}