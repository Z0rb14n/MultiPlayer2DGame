package util;

import java.util.Objects;

public class Pair<FirstClass, SecondClass> {
    public final FirstClass first;
    public final SecondClass second;

    public Pair(FirstClass first, SecondClass b) {
        this.first = first;
        this.second = b;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair<?,?> p = (Pair<?,?>) o;
        return first.equals(p.first) && second.equals(p.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
