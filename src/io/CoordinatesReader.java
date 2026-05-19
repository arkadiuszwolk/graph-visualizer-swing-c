package io;

import models.Graph;
import models.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class CoordinatesReader {
    public void updateCoordinates(String filePath, Graph graph) throws FileNotFoundException {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            scanner.useLocale(Locale.US);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                if (parts.length >= 3) {
                    String id = parts[0];
                    try {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        Vertex v = graph.getVertex(id);

                        if (v != null) {
                            v.setX(x);
                            v.setY(y);
                        } else {
                            System.err.println("Ostrzeżenie: Znaleziono współrzędne dla nieistniejącego wierzchołka: " + id);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd formatu liczb w linii: " + line);
                    }
                }
            }
        }
    }
}
