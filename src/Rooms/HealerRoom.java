package Rooms;

import java.util.Objects;
import java.util.Random;

import model.Healer;

// Represents a room containing a healer that can restore the hero's health
public class HealerRoom implements RoomType {
    private final Healer healer;
    private boolean healed;

    // Ensure healer instance is never null
    public HealerRoom(Healer healer) {
        Objects.requireNonNull(healer, "healer cannot be null");
        this.healer = healer;
        this.healed = false;
    }

    public HealerRoom() {
        // TODO Auto-generated constructor stub
        this(generateHealer());
    }

    private static Healer generateHealer() {
        Random random = new Random();
        return new Healer(random.nextInt(15) + 5);
    }

    @Override
    public String getTypeName() {
        return "HEALER";
    }

    public Healer getHealer() {
        return healer;
    }

    public boolean getHealed() {
        // TODO Auto-generated method stub
        return healed;
    }

    public void setHealed(boolean h) {
        // TODO Auto-generated method stub
        healed = h;
    }

}
