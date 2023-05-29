package game;

// TODO PROPER ID HANDLING LOL
public class PlayerID {
    private int id;

    public PlayerID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerID) {
            return ((PlayerID) obj).id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
