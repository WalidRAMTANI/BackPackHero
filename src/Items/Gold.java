package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;
import model.BackPack;
import model.Hero;

/**
 * Represents a Gold item.
 * <p>
 * Gold is a special item that stores a gold value and occupies
 * space in the backpack grid.
 * </p>
 */
public class Gold implements Item {

    /*
     * =========================
     * Instance fields
     * =========================
     */

    private String name;
    private int x;
    private int y;

    private int rarity;
    private int goldValue;
    private ArrayList<Point> occupiedCases;

    /*
     * =========================
     * Constructors
     * =========================
     */

    /**
     * Full constructor.
     */
    public Gold(String name,
            int x,
            int y,
            int rarity,
            int goldValue,
            ArrayList<Point> occupiedCases) {

        // Validation (equivalent to record compact constructor)
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases must not be null");

        if (goldValue < 0) {
            throw new IllegalArgumentException("goldValue < 0");
        }

        this.name = name;
        this.x = x;
        this.y = y;
        this.rarity = rarity;
        this.goldValue = goldValue;
        this.occupiedCases = occupiedCases;
    }

    /**
     * Convenience constructor.
     * Creates a Gold item with default position (1,1)
     * and a single occupied case at (0,0).
     */
    public Gold(String name, int rarity, int goldValue) {
        this(
                name,
                1,
                1,
                rarity,
                goldValue,
                new ArrayList<>(List.of(new Point(0, 0))));
    }

    /*
     * =========================
     * Getters (same names as fields)
     * =========================
     */

    public String name() {
        return name;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int rarity() {
        return rarity;
    }

    public int goldValue() {
        return goldValue;
    }

    public ArrayList<Point> occupiedCases() {
        return occupiedCases;
    }

    public String description() {
        return "A pile of " + goldValue + " gold coins.";
    }

    public void setGoldValue(int g) {
        goldValue = g;
    }
    /*
     * =========================
     * Other methods
     * =========================
     */

    /**
     * Returns the icon path for this gold item.
     * (Placeholder for future implementation)
     */
    public String iconPath() {
        return "";
    }

    /*
     * =========================
     * Static helpers
     * =========================
     */

    /**
     * Checks if the backpack contains at least one Gold item.
     */
    public static boolean hasGold(BackPack b) {
        Objects.requireNonNull(b);
        return b.getItems()
                .keySet()
                .stream()
                .anyMatch(i -> i instanceof Gold);
    }

    /**
     * Returns the position of the first Gold item found in the backpack grid.
     */
    public static Point getGold(BackPack b) {
        Objects.requireNonNull(b);

        // Scan the grid to find gold position
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (b.getGrille()[i][j] instanceof Gold) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    /*
     * =========================
     * Factory / cloning
     * =========================
     */

    @Override
    public Item createNewInstance() {
        return new Gold(
                name,
                rarity,
                goldValue);
    }

    @Override
    public ArrayList<Effect> effects() {
        // TODO Auto-generated method stub
        return null;
    }

    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
    }

    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
    }

}
