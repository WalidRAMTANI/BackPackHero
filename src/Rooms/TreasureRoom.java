package Rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import Items.Item;

/**
 * Represents a Treasure Room that holds a collection of treasure items.
 * The Hero can interact with the treasures by moving items between their
 * backpack and the room.
 */
public class TreasureRoom implements RoomType {

    private final List<Item> treasures;
    private boolean needKey;

    /**
     * Main constructor with null check
     */
    public TreasureRoom(List<Item> treasures) {
        Objects.requireNonNull(treasures);
        Random random = new Random();
        this.needKey = random.nextBoolean();
        this.treasures = Objects.requireNonNull(treasures, "treasures cannot be null");

    }

    public List<Item> treasures() {
        return treasures;
    }

    public boolean needKey() {
        return needKey;
    }

    public void setneedKey(boolean b) {
        needKey = b;
    }

    /**
     * Default constructor
     */
    public TreasureRoom() {
        this(new ArrayList<>(Item.generateItems()));
    }

    /**
     * Getter (replacement for record accessor)
     */
    public List<Item> getTreasures() {
        return treasures;
    }

    /**
     * Returns the type name of this room.
     */
    @Override
    public String getTypeName() {
        return "TREASURE";
    }

}
