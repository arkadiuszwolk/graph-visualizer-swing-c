package gui;

import models.Graph;
import models.Vertex;
import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private final Graph graph;
    private final State state;

    // Etykiety dla informacji o grafie
    private JLabel labelVertexCount = new JLabel("Wierzchołki: 0");
    private JLabel labelEdgeCount = new JLabel("Krawędzie: 0");
    private JLabel labelDimensions = new JLabel("Wymiary: 0 x 0");

    // Etykiety dla informacji o zaznaczonym wierzchołku
    private JLabel labelNodeName = new JLabel("Nazwa: -");
    private JLabel labelNodePosition = new JLabel("Pozycja X,Y: -");
    private JLabel labelNodeEdges = new JLabel("Krawędzie incyd.: 0");

    public InfoPanel(Graph graph, State state) {
        this.graph = graph;
        this.state = state;

        setPreferredSize(new Dimension(300, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- SEKCOJA 1: OGÓLNE ---
        JPanel pnlGeneral = new JPanel();
        pnlGeneral.setLayout(new BoxLayout(pnlGeneral, BoxLayout.Y_AXIS));
        pnlGeneral.setBorder(BorderFactory.createTitledBorder("Informacje o grafie"));

        setupLabel(labelVertexCount, pnlGeneral);
        setupLabel(labelEdgeCount, pnlGeneral);
        setupLabel(labelDimensions, pnlGeneral);

        // --- SEKCJA 2: SZCZEGÓŁY WIERZCHOŁKA ---
        JPanel pnlDetails = new JPanel();
        pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));
        pnlDetails.setBorder(BorderFactory.createTitledBorder("Zaznaczony wierzchołek"));

        setupLabel(labelNodeName, pnlDetails);
        setupLabel(labelNodePosition, pnlDetails);
        setupLabel(labelNodeEdges, pnlDetails);

        add(pnlGeneral);
//        add(Box.createVerticalStrut(10)); // Odstęp
        add(pnlDetails);
        add(Box.createVerticalGlue()); // Pcha wszystko do góry
    }

    public void updateInfo() {
        labelVertexCount.setText("Wierzchołki: " + graph.getVertices().size());
        labelEdgeCount.setText("Krawędzie: " + graph.getEdges().size());

        // Obliczanie wymiarów (Bounding Box)
        if (!graph.getVertices().isEmpty()) {
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            for (Vertex v : graph.getVertices()) {
                minX = Math.min(minX, v.getX()); maxX = Math.max(maxX, v.getX());
                minY = Math.min(minY, v.getY()); maxY = Math.max(maxY, v.getY());
            }
            labelDimensions.setText(String.format("Wymiary: %.1f x %.1f", (maxX - minX), (maxY - minY)));
        }

        // Szczegóły zaznaczonego
        Vertex sel = state.selectedVertex;
        if (sel != null) {
            labelNodeName.setText("Nazwa: " + sel.getId());
            labelNodePosition.setText(String.format("Pozycja X,Y: %.1f, %.1f", sel.getX(), sel.getY()));
            // Liczenie krawędzi dla tego wierzchołka
            long count = graph.getEdges().stream()
                    .filter(e -> e.getStart().equals(sel) || e.getEnd().equals(sel))
                    .count();
            labelNodeEdges.setText("Krawędzie incyd.: " + count);
        } else {
            labelNodeName.setText("Nazwa: -");
            labelNodePosition.setText("Pozycja X,Y: -");
            labelNodeEdges.setText("Krawędzie incyd.: 0");
        }
    }

    private void setupLabel(JLabel label, JPanel parent) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        parent.add(label);
    }
}