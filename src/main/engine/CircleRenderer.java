package engine;

import physics.Vec2D;
import physics.shape.Circle;

import java.awt.*;

public class CircleRenderer implements RendererBehaviour {
    private final GameObject parent;
    private boolean fill;
    private Color color;

    public CircleRenderer(GameObject parent, boolean fill) {
        this(parent, Color.WHITE, fill);
    }

    public CircleRenderer(GameObject parent, Color color, boolean fill) {
        this.parent = parent;
        this.color = color;
        this.fill = fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public boolean getFill() {
        return fill;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void render(Graphics2D g) {
        Circle shape = (Circle) parent.getBehaviour(PhysicsBehaviour.class).getTranslatedShape();
        Vec2D pos = shape.getCenter();
        float radius = shape.getRadius();
        g.setColor(color);
        if (fill) g.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius), (int)(radius * 2), (int)(radius * 2));
        else g.drawOval((int)(pos.getX() - radius), (int)(pos.getY() - radius), (int)(radius * 2), (int)(radius * 2));
    }
}
