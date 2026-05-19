package gui;

import io.GraphReader;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import models.Graph;

public class MainFrame extends JFrame {
    private GraphState state;
    private GraphPanel graphPanel;
    private InfoPanel infoPanel;

    private final Graph graph;
    private String selectedAlgorithm = AlgorithmRunner.ALG_FR;
    private String currentEdgesFile  = null;

    public MainFrame(Graph graph) {
        this.graph = graph;

        setTitle("Graph Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        state      = new GraphState();
        graphPanel = new GraphPanel(graph, state);
        infoPanel  = new InfoPanel(graph, state);

        GraphController controller = new GraphController(graph, state, graphPanel, infoPanel);
        graphPanel.addMouseListener(controller);
        graphPanel.addMouseMotionListener(controller);
        graphPanel.addMouseWheelListener(controller);

        add(createToolBar(controller), BorderLayout.NORTH);
        add(graphPanel,                BorderLayout.CENTER);
        add(infoPanel,                 BorderLayout.EAST);

        setLocationRelativeTo(null);
        infoPanel.updateInfo();
    }

    private JToolBar createToolBar(GraphController controller) {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        // ── Oryginalne przyciski ──────────────────────────────────────────

        JButton btnReset   = new JButton("Resetuj układ");
        JButton btnCenter  = new JButton("Centruj Graf");
        JButton btnZoomIn  = new JButton("Zoom +");
        JButton btnZoomOut = new JButton("Zoom -");
        JButton btnSave    = new JButton("Zapisz Coords");

        btnReset.setToolTipText("Wczytuje współrzędne ponownie z pliku coords.txt");
        btnReset.addActionListener(e -> { graphPanel.refreshFromFile(); infoPanel.updateInfo(); });
        btnCenter.addActionListener(e -> { graphPanel.centerView(); infoPanel.updateInfo(); });
        btnZoomIn .addActionListener(e -> controller.zoomAroundCenter(1.2));
        btnZoomOut.addActionListener(e -> controller.zoomAroundCenter(0.8));

        // ── Nowy: Wczytaj graf (automatycznie odpala algorytm) ────────────

        JButton btnLoad = new JButton("Wczytaj Graf");
        btnLoad.setToolTipText("Wybierz plik z krawędziami — algorytm uruchomi się automatycznie");
        btnLoad.addActionListener(e -> loadGraphFromFile());

        // ── Nowy: Wybór algorytmu ─────────────────────────────────────────

        JComboBox<String> comboAlg = new JComboBox<>(new String[]{
            "Fruchterman-Reingold",
            "Tutte (sprężyny)"
        });
        comboAlg.setMaximumSize(new Dimension(185, 26));
        comboAlg.setToolTipText("Algorytm używany przy następnym wczytaniu");
        comboAlg.addActionListener(e -> {
            selectedAlgorithm = (comboAlg.getSelectedIndex() == 0)
                ? AlgorithmRunner.ALG_FR
                : AlgorithmRunner.ALG_TUTTE;
            runAlgorithm();
        });

        // ── Składanie toolbara ────────────────────────────────────────────

        bar.add(btnLoad);
        bar.add(btnReset);
        bar.add(btnCenter);
        bar.addSeparator();
        bar.add(btnZoomIn);
        bar.add(btnZoomOut);
        bar.addSeparator();
        bar.add(new JLabel(" Algorytm: "));
        bar.add(comboAlg);
        bar.add(Box.createHorizontalGlue());
        bar.add(btnSave);

        return bar;
    }

    private void loadGraphFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Wybierz plik z krawędziami grafu");
        chooser.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));
        chooser.setCurrentDirectory(new File("src/data"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try {
            graph.clear();
            currentEdgesFile = file.getAbsolutePath().replace("\\", "/");
            new GraphReader().readFromTextFile(file.getAbsolutePath(), graph);
            graphPanel.centerView();
            infoPanel.updateInfo();
            System.out.println("Wczytano: " + file.getPath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd wczytywania pliku:\n" + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Automatycznie uruchamiamy algorytm po wczytaniu
        runAlgorithm();
        infoPanel.updateInfo();
    }

    private void runAlgorithm() {
        if (currentEdgesFile == null) return;
        AlgorithmRunner.run(graph, selectedAlgorithm, currentEdgesFile, graphPanel, null);
        infoPanel.updateInfo();
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}