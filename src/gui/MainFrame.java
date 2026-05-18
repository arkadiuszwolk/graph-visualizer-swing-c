package gui;

import models.Graph;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private State state;
    private GraphPanel graphPanel;
    private InfoPanel infoPanel;

    public MainFrame(Graph graph) {
        setTitle("Graph Interactor Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        state = new State();
        graphPanel = new GraphPanel(graph, state);
        infoPanel = new InfoPanel(graph, state);

        Controller controller = new Controller(graph, state, graphPanel, infoPanel);

        graphPanel.addMouseListener(controller);
        graphPanel.addMouseMotionListener(controller);
        graphPanel.addMouseWheelListener(controller);

        JToolBar toolBar = createToolBar(controller);

        add(toolBar, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);

        setLocationRelativeTo(null);

        infoPanel.updateInfo();
    }

    private JToolBar createToolBar(Controller controller) {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton btnCenter = new JButton("Centruj Graf");
        JButton btnZoomIn = new JButton("Zoom +");
        JButton btnZoomOut = new JButton("Zoom -");
        JButton btnSave = new JButton("Zapisz Coords");
        JButton btnReset = new JButton("Resetuj układ");

        btnReset.setToolTipText("Wczytuje współrzędne ponownie z pliku coords.txt");

        btnReset.addActionListener(e -> {
            controller.refreshFromFile();
            infoPanel.updateInfo(); // Aktualizujemy info po resecie
        });

        btnCenter.addActionListener(e -> {
            graphPanel.centerView();
            infoPanel.updateInfo();
        });

        btnZoomIn.addActionListener(e -> controller.zoomAroundCenter(1.2));
        btnZoomOut.addActionListener(e -> controller.zoomAroundCenter(0.8));

        bar.add(btnReset);
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