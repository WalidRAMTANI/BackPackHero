package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;
import effects.PoisonEffect;
import model.Hero;

/**
 * Represents a Shield item that provides protection to the Hero.
 */
public class Shield implements Item {
    public static int ID = 0;

    private final int id;
    private final String name;
    private int x;
    private int y;
    private final int energyCost;
    private final int rarity;
    private int protectionValue;
    private final String description;
    private final ArrayList<Effect> effects;
    private final ArrayList<Point> occupiedCases;

    /**
     * @param id              Unique ID
     * @param name            Shield name
     * @param x               Grid width
     * @param y               Grid height
     * @param energyCost      Energy consumed per use
     * @param rarity          Item rarity
     * @param protectionValue Protection provided
     * @param description     Brief description
     * @param effects         Associated effects
     * @param occupiedCases   Relative grid points occupied
     */
    public Shield(int id, String name, int x, int y, int energyCost, int rarity,
            int protectionValue, String description,
            ArrayList<Effect> effects, ArrayList<Point> occupiedCases) {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(effects, "effects cannot be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases cannot be null");

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.energyCost = Math.max(0, energyCost);
        this.rarity = rarity;
        this.protectionValue = Math.max(0, protectionValue);
        this.description = description;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
    }

    /**
     * Constructor with automatic ID generation.
     */
    public Shield(String name, int x, int y, int energyCost, int rarity,
            int protectionValue, String description,
            ArrayList<Effect> effects, ArrayList<Point> occupiedCases) {
        this(ID++, name, x, y, energyCost, rarity, protectionValue, description, effects, occupiedCases);
    }

    /** @return Item ID */
    public int id() {
        return id;
    }

    /** @return Item Name */
    public String name() {
        return name;
    }

    /** @return Width in cells */
    public int x() {
        return x;
    }

    /** @return Height in cells */
    public int y() {
        return y;
    }

    /** @return Energy cost per use */
    public int energyCost() {
        return energyCost;
    }

    /** @return Rarity level */
    public int rarity() {
        return rarity;
    }

    /** @return Protection value */
    public int protectionValue() {
        return protectionValue;
    }

    /** @param p New protection value */
    public void setProtectionValue(int p) {
        protectionValue = p;
    }

    /** @return Item description */
    public String description() {
        return description;
    }

    /** @return Associated effects */
    public ArrayList<Effect> effects() {
        return effects;
    }

    /** @return Occupied grid points */
    public ArrayList<Point> occupiedCases() {
        return occupiedCases;
    }

    @Override
    public Item createNewInstance() {
        return new Shield(name, x, y, energyCost, rarity, protectionValue, description,
                new ArrayList<>(effects), new ArrayList<>(occupiedCases));
    }

    @Override
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        if (energyCost <= hero.getEnergy()) {
            hero.setProtection(hero.getProtection() + protectionValue);
            hero.setEnergy(hero.getEnergy() - energyCost);
        }
        for (Effect ef : effects) {
            if (ef instanceof PoisonEffect) {
                ef.execute(hero, enemies, e, this);
            }
        }
    }

    @Override
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        for (Effect ef : effects) {
            if (!(ef instanceof PoisonEffect)) {
                ef.execute(hero, enemies, e, this);
            }
        }
    }

    /** Resets the shield's state based on its identity. */
    public void baseShape() {
        if (name.equals("Rough Buckler") || name.equals("Knight's Shield")) {
            setProtectionValue(7);
        } else {
            setProtectionValue(6);
        }
    }
}
