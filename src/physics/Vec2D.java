package physics;

import java.io.Serializable;
import java.util.Objects;

public class Vec2D implements Serializable {
    private static final long serialVersionUID = 69420L;
    public static final Vec2D ZERO = new Vec2D(0,0);
    public static final Vec2D UP = new Vec2D(0,-1);
    public static final Vec2D DOWN = new Vec2D(0,1);
    public static final Vec2D LEFT = new Vec2D(-1,0);
    public static final Vec2D RIGHT = new Vec2D(1,0);
    private float x;
    private float y;

    public static double dist(Vec2D v1, Vec2D v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
    public static Vec2D reflect(Vec2D L, Vec2D N) {
        return L.sub(N.mult(2*L.dot(N)));
    }

    public Vec2D() {
        this(0,0);
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vec2D add(float x, float y) {
        return new Vec2D(this.x + x, this.y + y);
    }

    public Vec2D add(Vec2D vec) {
        return this.add(vec.x,vec.y);
    }

    public Vec2D sub(float x, float y) {
        return new Vec2D(this.x - x, this.y - y);
    }

    public Vec2D sub(Vec2D vec) {
        return this.sub(vec.x,vec.y);
    }

    public Vec2D mult(float scalar) {
        return new Vec2D(x*scalar,y*scalar);
    }

    public Vec2D perp() {
        return new Vec2D(-y,x);
    }

    public float sqMag() {
        return x*x + y*y;
    }

    public float mag() {
        return (float) Math.sqrt(x*x + y*y);
    }

    public float dot(Vec2D vec) {
        return x*vec.x + y*vec.y;
    }

    public Vec2D rotated(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return new Vec2D(x*cos - y*sin, x*sin + y*cos);
    }

    public Vec2D normalize() {
        float mag = mag();
        if (mag == 0) {
            //System.out.println("Warning: Normalizing zero vector; returning zero vector");
            return ZERO;
        }
        return new Vec2D(x/mag,y/mag);
    }

    public Vec2D scaleTo(float mag) {
        return normalize().mult(mag);
    }

    public Vec2D copy() {
        return new Vec2D(x,y);
    }

    public float proj(Vec2D axis) {
        return this.dot(axis);
    }

    public float projNorm(Vec2D axis) {
        return proj(axis.normalize());
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vec2D)) return false;
        Vec2D v = (Vec2D) o;
        return v.x == x && v.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
