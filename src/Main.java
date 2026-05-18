import models.Graph;
import io.GraphReader;
import io.CoordinatesReader;
import gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        GraphReader gReader = new GraphReader();
        CoordinatesReader cReader = new CoordinatesReader();

        try {
            System.out.println("Wczytywanie struktury grafu...");
            gReader.loadGraph("src/data/edges.txt", graph);

            System.out.println("Wczytywanie współrzędnych...");
            cReader.updateCoordinates("src/data/coords.txt", graph);

            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(graph);
                frame.setVisible(true);

                frame.getGraphPanel().centerView();
            });

        } catch (Exception e) {
            System.err.println("Błąd krytyczny podczas startu aplikacji:");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            JOptionPane.showMessageDialog(null,
                    "Nie udało się wczytać plików grafu!\n" + e.getMessage(),
                    "Błąd Startu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}