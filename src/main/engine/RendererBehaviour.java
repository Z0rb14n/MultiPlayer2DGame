package engine;

import java.awt.Graphics2D;

public interface RendererBehaviour extends GameObjectBehaviour {
    void render(Graphics2D g);
}
