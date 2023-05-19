package engine;

import engine.GameObject;
import engine.PhysicsBehaviour;
import engine.RendererBehaviour;
import physics.Vec2D;

import java.awt.*;

public class TriangleRenderer implements RendererBehaviour {
    private final GameObject parent;
    private Color color;
    private boolean fill;
    public TriangleRenderer(GameObject parent, Color color, boolean fill) {
        this.parent = parent;
        this.color = color;
        this.fill = fill;
    }

    @Override
    public void render(Graphics2D g) {
        Vec2D[] points = parent.getBehaviour(PhysicsBehaviour.class).getTranslatedShape().getVertices();
        g.setColor(color);
        if (fill) {
            g.fillPolygon(new int[]{(int)points[0].getX(), (int)points[1].getX(), (int)points[2].getX()}, new int[]{(int)points[0].getY(), (int)points[1].getY(), (int)points[2].getY()}, 3);
        } else {
            g.drawPolygon(new int[]{(int)points[0].getX(), (int)points[1].getX(), (int)points[2].getX()}, new int[]{(int)points[0].getY(), (int)points[1].getY(), (int)points[2].getY()}, 3);
        }
    }
}
