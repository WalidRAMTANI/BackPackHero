package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;
import model.Hero;

/**
 * Represents an Armor item.
 * <p>
 * An Armor is an Item that provides protection to the Hero.
 * It consumes energy when used but does not disappear after usage.
 * </p>
 */
public class Armor implements Item {

    /*
     * =========================
     * Static fields
     * =========================
     */

    /**
     * Auto-incremented ID counter for Armors
     */
    public static int ID = 0;

    /*
     * =========================
     * Instance fields
     * =========================
     */

    private int id;
    private String name;
    private int x;
    private int y;

    private int energyCost;
    private int rarity;
    private int protectionValue;
    private String description;

    private ArrayList<Effect> effects;
    private ArrayList<Point> occupiedCases;

    /*
     * =========================
     * Constructors
     * =========================
     */

    /**
     * Full constructor.
     * Used internally and for cloning.
     */
    public Armor(int id,
            String name,
            int x,
            int y,
            int energyCost,
            int rarity,
            int protectionValue,
            String description,
            ArrayList<Effect> effects,
            ArrayList<Point> occupiedCases) {

        // Field validation (equivalent to record compact constructor)
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases must not be null");

        if (energyCost < 0) {
            throw new IllegalArgumentException("energyCost < 0");
        }
        if (protectionValue < 0) {
            throw new IllegalArgumentException("protectionValue < 0");
        }

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.energyCost = energyCost;
        this.rarity = rarity;
        this.protectionValue = protectionValue;
        this.description = description;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
    }

    /**
     * Constructor with automatic ID generation.
     */
    public Armor(String name,
            int x,
            int y,
            int energyCost,
            int rarity,
            int protectionValue,
            String description,
            ArrayList<Effect> effects,
            ArrayList<Point> occupiedCases) {

        this(ID++, name, x, y, energyCost, rarity, protectionValue,
                description, effects, occupiedCases);
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int energyCost() {
        return energyCost;
    }

    public int rarity() {
        return rarity;
    }

    public int protectionValue() {
        return protectionValue;
    }

    public String description() {
        return description;
    }

    public ArrayList<Effect> effects() {
        return effects;
    }

    public ArrayList<Point> occupiedCases() {
        return occupiedCases;
    }

    public void setProtection(int protection) {
        if (protection < 0)
            return;
        protectionValue = protection;
    }

    /*
     * =========================
     * Game logic
     * =========================
     */

    /**
     * Uses the armor.
     * <p>
     * The armor increases the hero's protection and consumes energy.
     * It does NOT disappear after use.
     * </p>
     *
     * @param hero  the hero using the armor
     * @param e     enemy (unused, armor affects only the hero)
     * @param value protection value to add
     * @return false (armor is not consumed)
     */
    public boolean use_item(Hero hero, Enemy e, int value) {
        Objects.requireNonNull(hero);

        if (hero.getEnergy() >= energyCost) {
            hero.setEnergy(hero.getEnergy() - energyCost);
            hero.setProtection(hero.getProtection() + value);
        }

        return false;
    }

    /*
     * =========================
     * Factory / cloning
     * =========================
     */

    /**
     * Creates a new independent instance of this Armor.
     */
    @Override
    public Item createNewInstance() {
        return new Armor(
                name,
                x,
                y,
                energyCost,
                rarity,
                protectionValue,
                description,
                effects,
                new ArrayList<>(occupiedCases));
    }

    /*
     * "Tunic", "Adds 5 Block\nAdjacent and diagonal Armor gets +1 Block",
     * "Chainmail", "Adds 10 Block",
     * "Wizards Robe","Adds 6 Block\n+2 damage for each weapon on same row",
     * 
     */
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);

        if (this instanceof Armor armor &&
                (armor.name().equals("Tunic") || armor.name().equals("Wizards Robe"))) {

            for (Effect ef : armor.effects()) {
                ef.execute(hero, enemies, e, armor);
            }
        }
    }

    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);

        if (this instanceof Armor armor) {
            use_item(hero, e, protectionValue);

        }
    }

    public void baseShape() {
        if (this instanceof Armor armor) {
            if (armor.name.equals("Tunic")) {
                armor.setProtection(5);
            } else if (armor.name.equals("Chainmail")) {
                armor.setProtection(10);
            } else {
                armor.setProtection(6);
            }
        }
    }
}
