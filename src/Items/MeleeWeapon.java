package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;
import model.Hero;

/**
 * Represents a melee weapon item.
 */
public class MeleeWeapon implements Weapon, Item {
    public static int ID = 0;

    private int id;
    private String name;
    private int x;
    private int y;
    private int energyCost;
    private int rarity;
    private int damage;
    private boolean destroy;
    private ArrayList<Effect> effects;
    private ArrayList<Point> occupiedCases;
    private String description;

    /**
     * @param id            Unique ID
     * @param name          Name
     * @param x             Grid width
     * @param y             Grid height
     * @param energyCost    Energy cost per strike
     * @param rarity        Item rarity
     * @param damage        Base damage
     * @param destroy       true if destroyed after use
     * @param effects       Associated effects
     * @param occupiedCases Relative grid points occupied
     * @param description   Brief description
     */
    public MeleeWeapon(int id, String name, int x, int y, int energyCost, int rarity, int damage,
            boolean destroy, ArrayList<Effect> effects, ArrayList<Point> occupiedCases, String description) {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases cannot be null");
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.energyCost = Math.max(0, energyCost);
        this.rarity = rarity;
        this.damage = Math.max(0, damage);
        this.destroy = destroy;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
        this.description = description;
    }

    /**
     * Constructor with automatic ID generation.
     */
    public MeleeWeapon(String name, int x, int y, int energyCost, int rarity, int damage,
            boolean destroy, ArrayList<Effect> effects, ArrayList<Point> occupiedCases, String description) {
        this(ID++, name, x, y, energyCost, rarity, damage, destroy, effects, occupiedCases, description);
    }

    /** @param atta Damage value to add */
    public void setAttack(int atta) {
        damage += atta;
    }

    /** @return Item ID */
    public int id() {
        return id;
    }

    /** @return Name */
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

    /** @return Damage value */
    public int damage() {
        return damage;
    }

    /** @return true if consumed */
    public boolean destroy() {
        return destroy;
    }

    /** @return Associated effects */
    public ArrayList<Effect> effects() {
        return effects;
    }

    /** @return Occupied grid points */
    public ArrayList<Point> occupiedCases() {
        return occupiedCases;
    }

    /** @return Item description */
    public String description() {
        return description;
    }

    @Override
    public Item createNewInstance() {
        return new MeleeWeapon(name, x, y, energyCost, rarity, damage, destroy,
                new ArrayList<>(effects), new ArrayList<>(occupiedCases), description);
    }

    @Override
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        Objects.requireNonNull(e);
        if (hero.getEnergy() >= energyCost) {
            hero.setEnergy(hero.getEnergy() - energyCost);
            e.getAttacked(damage);
            if (name.equals("Hatchet")) {
                for (Effect ef : effects) {
                    ef.execute(hero, enemies, e, this);
                }
            }
        }
    }

    @Override
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        if (name.equals("Bowblade")) {
            for (Effect ef : effects) {
                ef.execute(hero, enemies, e, this);
            }
        }
    }

    /** Resets the weapon's base damage based on its identity. */
    public void baseShape() {
        if (name.equals("Wooden Sword")) {
            setAttack(6);
        } else if (name.equals("Bowblade")) {
            setAttack(15);
        } else {
            setAttack(20);
        }
    }
}
