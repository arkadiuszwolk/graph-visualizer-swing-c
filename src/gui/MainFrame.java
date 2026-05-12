package gui;

import gui.GraphController;
import gui.GraphPanel;
import gui.GraphState;
import models.Graph;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private GraphPanel graphPanel;
    private GraphState state;

    public MainFrame(Graph graph) {
        setTitle("Graph Interactor Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        state = new GraphState();
        graphPanel = new GraphPanel(graph, state);

        // 1. Tworzymy kontroler
        GraphController controller = new GraphController(graph, state, graphPanel);

        graphPanel.addMouseListener(controller);
        graphPanel.addMouseMotionListener(controller);
        graphPanel.addMouseWheelListener(controller);

        // 2. Przekazujemy controller do metody createToolBar
        JToolBar toolBar = createToolBar(controller);

        add(toolBar, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    // 3. Dodajemy argument GraphController controller
    private JToolBar createToolBar(GraphController controller) {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnCenter = new JButton("Centruj Graf");
        JButton btnZoomIn = new JButton("Zoom +");
        JButton btnZoomOut = new JButton("Zoom -");
        JButton btnSave = new JButton("Zapisz Coords");
        JButton btnReset = new JButton("Resetuj układ");
        btnReset.setToolTipText("Wczytuje współrzędne ponownie z pliku coords.txt");

        btnReset.addActionListener(e -> {
            // Wywołujemy odświeżanie z panelu
            graphPanel.refreshFromFile();
        });

// Dodajemy do paska (np. obok przycisku Centruj)
        bar.add(btnReset);

        btnCenter.addActionListener(e -> graphPanel.centerView());

        // Teraz controller jest dostępny!
        btnZoomIn.addActionListener(e -> controller.zoomAroundCenter(1.2));
        btnZoomOut.addActionListener(e -> controller.zoomAroundCenter(0.8));

        bar.add(btnCenter);
        bar.addSeparator();
        bar.add(btnZoomIn);
        bar.add(btnZoomOut);
        bar.add(Box.createHorizontalGlue());
        bar.add(btnSave);

        return bar;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}