package gui;

import gui.GraphController;
import gui.GraphPanel;
import gui.GraphState;
import models.Graph;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame(Graph graph) {
        // 1. Tworzymy stan widoku
        GraphState state = new GraphState();

        // 2. Tworzymy panel i przekazujemy mu stan
        GraphPanel panel = new GraphPanel(graph, state);

        // 3. Tworzymy kontroler i przekazujemy mu wszystko
        GraphController controller = new GraphController(graph, state, panel);

        // 4. Podpinamy kontroler pod panel
        panel.addMouseListener(controller);
        panel.addMouseMotionListener(controller);
        panel.addMouseWheelListener(controller);

        add(panel);
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}