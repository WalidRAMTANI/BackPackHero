package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;
import effects.NumberUses;
import model.Hero;

/**
 * Represents a potion item that can be used for healing or other effects.
 */
public class Potion implements Item {
    public static int ID = 0;

    private int id;
    private String name;
    private int x;
    private int y;
    private int rarity;
    private boolean destroy;
    private String description;
    private ArrayList<Effect> effects;
    private ArrayList<Point> occupiedCases;

    /**
     * @param id            Unique ID
     * @param name          Potion name
     * @param x             Grid width
     * @param y             Grid height
     * @param rarity        Item rarity
     * @param destroy       true if destroyed after use
     * @param description   Brief description
     * @param effects       List of associated effects
     * @param occupiedCases Relative grid points occupied
     */
    public Potion(int id, String name, int x, int y, int rarity, boolean destroy, String description,
            ArrayList<Effect> effects, ArrayList<Point> occupiedCases) {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases cannot be null");
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.rarity = rarity;
        this.destroy = destroy;
        this.description = description;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
    }

    /**
     * @param name        Name
     * @param rarity      Rarity
     * @param description Description
     * @param effects     Effects list
     */
    public Potion(String name, int rarity, String description, ArrayList<Effect> effects) {
        this(ID++, name, 1, 1, rarity, true, description, effects, new ArrayList<>(List.of(new Point(0, 0))));
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

    /** @return Rarity level */
    public int rarity() {
        return rarity;
    }

    /** @return true if consumed */
    public boolean destroy() {
        return destroy;
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
        return new Potion(name, rarity, description, new ArrayList<>(effects));
    }

    @Override
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        boolean shouldRemove = false;
        for (Effect ef : effects) {
            ef.execute(hero, enemies, e, this);
            if (ef instanceof NumberUses eff && eff.n() <= 0) {
                shouldRemove = true;
            }
        }
        if (shouldRemove) {
            var position = hero.getBackpack().getItems().get(this).getFirst();
            hero.getBackpack().removeItem(position.x, position.y);
        }
    }

    @Override
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
    }
}
