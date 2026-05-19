package gui;

import io.CoordinatesReader;
import java.io.*;
import javax.swing.*;
import models.Graph;

public class AlgorithmRunner {

    // Numer algorytmu wg dokumentacji: 1 = Fruchterman-Reingold, 2 = Tutte
    public static final String ALG_FR    = "1";
    public static final String ALG_TUTTE = "2";

    private static final String EXEC_PATH = System.getProperty("os.name")
            .toLowerCase().contains("win") ? "./graph.exe" : "./graph";

    private static final File PROJECT_DIR = new File(System.getProperty("user.dir"));

    private static final String OUTPUT_PATH =
            new File(System.getProperty("user.dir"), "src/data/coords.txt")
                    .getAbsolutePath().replace("\\", "/");
    private static final String OUTPUT_ARG = OUTPUT_PATH;

    private static final int ITERATIONS = 500;

    /**
     * Uruchamia ./graph z wybranym algorytmem i podanym plikiem krawędzi.
     * Po sukcesie wczytuje nowe współrzędne do grafu i odświeża panel.
     *
     * Wywołanie: ./graph -n <edgesFile> -a <1|2> -o src/data/coords -f txt -i 500
     */
    public static boolean run(Graph graph, String algorithm, String edgesFile,
                              GraphPanel panel, JComponent parent) {

        String[] cmd = {
            EXEC_PATH,
            "-n", edgesFile,
            "-a", algorithm,
            "-o", OUTPUT_ARG,
            "-f", "txt",
            "-i", String.valueOf(ITERATIONS)
        };

        try {
            System.out.println("Uruchamiam: " + String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            pb.directory(PROJECT_DIR);

            Process process = pb.start();

            // Czytamy stdout/stderr żeby nie zablokować procesu
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println("[graph-exec] " + line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                JOptionPane.showMessageDialog(parent,
                        "Błąd wykonania (kod " + exitCode + " — " + describeExitCode(exitCode) + "):\n\n" + output,
                        "Błąd algorytmu", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Wczytujemy wynikowe współrzędne i centrujemy widok
            new CoordinatesReader().updateCoordinates(OUTPUT_PATH, graph);
            panel.centerView();
            panel.repaint();
            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                    "Nie można uruchomić: " + EXEC_PATH + "\n" +
                    "Upewnij się że program C jest skompilowany (make).\n\n" + e.getMessage(),
                    "Błąd uruchomienia", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(parent,
                    "Algorytm został przerwany.",
                    "Przerwano", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private static String describeExitCode(int code) {
        return switch (code) {
            case 1 -> "brak pliku wejściowego";
            case 2 -> "błąd formatu danych";
            case 3 -> "błąd zapisu pliku wyjściowego";
            case 4 -> "nieprawidłowy numer algorytmu";
            default -> "nieznany błąd";
        };
    }
}