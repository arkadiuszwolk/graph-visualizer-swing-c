package gui;

import models.Vertex;
import java.awt.Point;

public class GraphState {
    public double zoomFactor = 1.0;
    public double offsetX = 0;
    public double offsetY = 0;

    public Vertex hoveredVertex = null;
    public Vertex selectedVertex = null;
    public boolean isDraggingVertex = false;

    public Point lastMousePosition = null;
}
