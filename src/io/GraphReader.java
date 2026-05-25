package io;

import models.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class GraphReader {
    public void readFromTextFile(String filePath, Graph graph) throws FileNotFoundException {
        File file = new File(filePath);
        try (Scanner scanner = new Scanner(file)) {
            scanner.useLocale(Locale.US);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                if (parts.length == 4) {
                    String label = parts[0];
                    String startId = parts[1];
                    String endId = parts[2];
                    try {
                        double weight = Double.parseDouble(parts[3]);
                        graph.addEdge(label, startId, endId, weight);
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd formatu wagi w linii: " + line);
                    }
                }
            }
        }
    }
}
