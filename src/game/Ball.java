package game;

import physics.CollisionListener;
import physics.PhysicsObject;
import physics.PhysicsUpdateListener;
import physics.Vec2D;
import physics.shape.Circle;

import java.awt.*;

public class Ball implements CollisionListener, PhysicsUpdateListener {
    public static int BALL_COLLISION_MASK = 0b01;
    private int numCollisions = 0;
    private final PhysicsObject circle;

    public Ball(Vec2D start) {
        circle = new PhysicsObject(new Circle(Vec2D.ZERO,2), start, false);
        circle.setCoefOfRestitution(1);
        circle.addCollisionListener(this);
        circle.addUpdateListener(this);
        circle.setCollisionMask(BALL_COLLISION_MASK);
    }

    public PhysicsObject getPhysicsObject() {
        return circle;
    }

    @Override
    public void onCollision(PhysicsObject listenerTarget, PhysicsObject collider, Vec2D mtv) {
        numCollisions++;
        if (numCollisions > 10) {
            GameController.getInstance().removeBall(this);
        }
    }

    public void render(Graphics2D g) {
        Vec2D center = ((Circle)circle.getTranslatedShape()).getCenter();
        float radius = ((Circle)circle.getTranslatedShape()).getRadius();
        g.setColor(Color.GREEN);
        g.drawOval((int)(center.getX() - radius), (int)(center.getY() - radius), (int)(radius * 2), (int)(radius * 2));
    }

    @Override
    public void onPhysicsUpdate(PhysicsObject source, float dt) {
        if (source.getPosition().getX() < 0 || source.getPosition().getY() < 0 || source.getPosition().getX() > GameController.GAME_WIDTH || source.getPosition().getY() > GameController.GAME_HEIGHT) {
            GameLogger.getDefault().log("Invalid ball position " + source.getPosition() + "; removing", "PHYSICS");
            GameController.getInstance().forceRemoveBall(this);
        }
    }
}
