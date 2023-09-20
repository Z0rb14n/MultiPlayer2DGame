package engine;

import physics.Vec2D;
import physics.shape.ConvexPolygon;

import java.awt.*;

public class ConvexPolygonRenderer implements RendererBehaviour {
    private final GameObject parent;
    private boolean fill;
    private Color color;

    public ConvexPolygonRenderer(GameObject parent) {
        this(parent, Color.BLACK);
    }

    public ConvexPolygonRenderer(GameObject parent, Color color) {
        this(parent, color, true);
    }

    public ConvexPolygonRenderer(GameObject parent, Color color, boolean fill) {
        this.parent = parent;
        this.color = color;
        this.fill = fill;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    @Override
    public void render(Graphics2D g) {
        ConvexPolygon polygon = (ConvexPolygon) parent.getBehaviour(PhysicsBehaviour.class).getTranslatedShape();
        g.setColor(color);
        Vec2D[] vertices = polygon.getVertices();
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
