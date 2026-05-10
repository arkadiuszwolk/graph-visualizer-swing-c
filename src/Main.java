import models.Graph;
import io.GraphReader;
import io.CoordinatesReader;
import gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Przygotowujemy pusty obiekt grafu
        Graph graph = new Graph();

        // 2. Inicjalizujemy czytniki
        GraphReader gReader = new GraphReader();
        CoordinatesReader cReader = new CoordinatesReader();

        try {
            // 3. Wczytujemy dane z Twoich plików w src/data/
            System.out.println("Wczytywanie struktury grafu...");
            gReader.readFromTextFile("src/data/edges.txt", graph);

            System.out.println("Wczytywanie współrzędnych...");
            cReader.updateCoordinates("src/data/coords.txt", graph);

            // 4. Uruchamiamy GUI w wątku zdarzeń Swinga
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(graph);
                frame.setVisible(true);

                // To sprawi, że graf "rozstrzeli się" z rogu na całe okno automatycznie
                frame.getGraphPanel().centerView();
            });

        } catch (Exception e) {
            // Jeśli pliki nie zostaną znalezione, wypiszemy błąd w konsoli
            System.err.println("Błąd krytyczny podczas startu aplikacji:");
            e.printStackTrace();

            // Opcjonalnie: pokazywanie błędu w małym okienku dla użytkownika
            JOptionPane.showMessageDialog(null,
                    "Nie udało się wczytać plików grafu!\n" + e.getMessage(),
                    "Błąd Startu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}