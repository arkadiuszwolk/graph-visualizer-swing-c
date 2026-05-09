package gui;

import models.Edge;
import models.Graph;
import models.Vertex;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {
    private final Graph graph;
    private final GraphState state;

    public GraphPanel(Graph graph, GraphState state) {
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
        // 1. Pozycje ekranowe końców
        int x1 = (int) (edge.getStart().getX() * state.zoomFactor + state.offsetX);
        int y1 = (int) (edge.getStart().getY() * state.zoomFactor + state.offsetY);
        int x2 = (int) (edge.getEnd().getX() * state.zoomFactor + state.offsetX);
        int y2 = (int) (edge.getEnd().getY() * state.zoomFactor + state.offsetY);

        // 2. Rysowanie samej linii (to już masz)
        if (edge.getStart() == state.selectedVertex || edge.getEnd() == state.selectedVertex) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
        } else {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1));
        }
        g2d.drawLine(x1, y1, x2, y2);

        // 3. WYŚWIETLANIE WAGI
        String weightText = String.valueOf(edge.getWeight());

        // Obliczamy środek odcinka
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;

        // Opcjonalnie: tło pod tekstem, żeby waga nie zlewała się z linią
        g2d.setFont(new Font("Arial", Font.ITALIC, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(weightText);
        int textHeight = fm.getAscent();

        // Rysujemy biały prostokąt (tło etykiety)
        g2d.setColor(new Color(255, 255, 255, 200)); // Półprzezroczysty biały
        g2d.fillRect(midX - textWidth / 2 - 2, midY - textHeight / 2 - 2, textWidth + 4, textHeight + 2);

        // Rysujemy tekst wagi
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(weightText, midX - textWidth / 2, midY + textHeight / 2);
    }

    private void drawVertex(Graphics2D g2d, Vertex v) {
        int r = 10;
        int x = (int) (v.getX() * state.zoomFactor + state.offsetX);
        int y = (int) (v.getY() * state.zoomFactor + state.offsetY);

        // Efekt Hover (jasna obwódka)
        if (v == state.hoveredVertex) {
            g2d.setColor(new Color(200, 200, 200, 150));
            g2d.fillOval(x - r - 5, y - r - 5, (r + 5) * 2, (r + 5) * 2);
        }

        // Kolor bazowy (zmienny jeśli zaznaczony)
        g2d.setColor(v == state.selectedVertex ? Color.ORANGE : Color.BLUE);
        g2d.fillOval(x - r, y - r, r * 2, r * 2);

        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - r, y - r, r * 2, r * 2);
        g2d.drawString(v.getId(), x + r + 2, y);
    }
}