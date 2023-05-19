package engine;

import physics.shape.AxisAlignedBoundingBox;

import java.awt.*;

public class BoxRenderer implements RendererBehaviour {
    private final GameObject parent;
    private boolean fill;
    private Color color;

    public BoxRenderer(GameObject parent) {
        this(parent, Color.BLACK);
    }

    public BoxRenderer(GameObject parent, Color color) {
        this(parent, color, true);
    }

    public BoxRenderer(GameObject parent, Color color, boolean fill) {
        this.parent = parent;
        this.color = color;
        this.fill = fill;
    }

    @Override
    public void render(Graphics2D g) {
        AxisAlignedBoundingBox box = parent.getBehaviour(PhysicsBehaviour.class).getTranslatedShape().getAABB();
        g.setColor(color);
        if (fill) g.fillRect((int)box.getBottomLeft().getX(), (int)box.getBottomLeft().getY(), (int)box.getWidth(), (int)box.getHeight());
        else g.drawRect((int)box.getBottomLeft().getX(), (int)box.getBottomLeft().getY(), (int)box.getWidth(), (int)box.getHeight());
    }
}
