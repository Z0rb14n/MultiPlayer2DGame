package engine;

import physics.Vec2D;
import physics.shape.RotatedRectangle;

import java.awt.*;

public class RotatedRectangleRenderer implements RendererBehaviour {
    private final GameObject parent;
    private boolean fill;
    private Color color;

    public RotatedRectangleRenderer(GameObject parent) {
        this(parent, Color.BLACK);
    }

    public RotatedRectangleRenderer(GameObject parent, Color color) {
        this(parent, color, true);
    }

    public RotatedRectangleRenderer(GameObject parent, Color color, boolean fill) {
        this.parent = parent;
        this.color = color;
        this.fill = fill;
    }
    @Override
    public void render(Graphics2D g) {
        RotatedRectangle box = (RotatedRectangle) parent.getBehaviour(PhysicsBehaviour.class).getTranslatedShape();
        g.setColor(color);
        Vec2D[] vertices = box.getVertices();
        int[] xPoints = new int[vertices.length];
        int[] yPoints = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            xPoints[i] = (int)vertices[i].getX();
            yPoints[i] = (int)vertices[i].getY();
        }
        if (fill) g.fillPolygon(xPoints, yPoints, vertices.length);
        else g.drawPolygon(xPoints, yPoints, vertices.length);
    }
}
