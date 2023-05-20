package util;

import java.util.Objects;

public class Pair<FirstClass, SecondClass> {
    private FirstClass a;
    private SecondClass b;

    public Pair() {
        this(null, null);
    }

    public Pair(FirstClass a, SecondClass b) {
        set(a, b);
    }

    public void set(FirstClass a, SecondClass b) {
        this.a = a;
        this.b = b;
    }

    public FirstClass getFirst() { return a; }
    public SecondClass getSecond() { return b; }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair p = (Pair) o;
        return a.equals(p.a) && b.equals(p.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a,b);
    }
}
