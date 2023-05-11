package physics.shape;

public class Range {
    private float lower;
    private float upper;

    public Range(float lower, float upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public float getLower() {
        return lower;
    }

    public float getUpper() {
        return upper;
    }

    public void setLower(float lower) {
        this.lower = lower;
    }

    public void setUpper(float upper) {
        this.upper = upper;
    }

    public boolean contains(float value) {
        return value >= lower && value <= upper;
    }

    public boolean contains(Range range) {
        return range.lower >= lower && range.upper <= upper;
    }

    public boolean overlaps(Range range) {
        return range.lower <= upper && range.upper >= lower;
    }

    public float getOverlapLength(Range range) {
        if (!overlaps(range)) {
            return 0;
        }
        return Math.min(upper, range.upper) - Math.max(lower, range.lower);
    }

    //https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf
    // should support the contains case???
    public float getIntervalMTV(Range range) {
        float aRight = range.getUpper() - lower;
        float aLeft = upper - range.getLower();
        if (aLeft < 0 || aRight < 0) {
            return Float.NaN;
        }
        if (aRight < aLeft) return aRight;
        return -aLeft;
    }

    public Range getOverlap(Range range) {
        if (!overlaps(range)) {
            return null;
        }
        return new Range(Math.max(lower, range.lower), Math.min(upper, range.upper));
    }

    public float getLength() {
        return upper - lower;
    }

    @Override
    public String toString() {
        return "[" + lower + ", " + upper + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range range = (Range) obj;
        return lower == range.lower && upper == range.upper;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(lower) ^ Float.hashCode(upper);
    }
}
