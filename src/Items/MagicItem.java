package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.AttackAllEnemies;
import effects.Effect;
import model.Hero;

/**
 * Represents a Magic Item.
 * <p>
 * A MagicItem consumes mana to deal damage to an enemy.
 * It may be destroyed after use depending on its configuration.
 * </p>
 */
public class MagicItem implements Item, Weapon {

    /*
     * =========================
     * Static fields
     * =========================
     */

    /**
     * Auto-incremented ID counter
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

    private int manaCost;
    private int rarity;
    private int damage;
    private boolean destroy;

    private ArrayList<Effect> effects;
    private ArrayList<Point> occupiedCases;
    private String description;

    /*
     * =========================
     * Constructors
     * =========================
     */

    /**
     * Full constructor.
     */
    public MagicItem(int id,
            String name,
            int x,
            int y,
            int manaCost,
            int rarity,
            int damage,
            boolean destroy,
            ArrayList<Effect> effects,
            ArrayList<Point> occupiedCases,
            String description) {

        // Validation (equivalent to record compact constructor)
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases must not be null");

        if (manaCost < 0) {
            throw new IllegalArgumentException("manaCost < 0");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("damage < 0");
        }

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.manaCost = manaCost;
        this.rarity = rarity;
        this.damage = damage;
        this.destroy = destroy;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
        this.description = description;
    }

    /**
     * Constructor with automatic ID generation.
     */
    public MagicItem(String name,
            int x,
            int y,
            int manaCost,
            int rarity,
            int damage,
            boolean destroy,
            ArrayList<Effect> effects,
            ArrayList<Point> occupiedCases,
            String description) {

        this(ID++, name, x, y, manaCost, rarity, damage,
                destroy, effects, occupiedCases, description);
    }

    /*
     * =========================
     * Getters (same names as fields)
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

    public int manaCost() {
        return manaCost;
    }

    public int rarity() {
        return rarity;
    }

    public int damage() {
        return damage;
    }

    public boolean destroy() {
        return destroy;
    }

    public ArrayList<Effect> effects() {
        return effects;
    }

    public ArrayList<Point> occupiedCases() {
        return occupiedCases;
    }

    public String description() {
        return description;
    }

    public void setAttack(int atta) {
        damage += atta;
    }
    /*
     * =========================
     * Other methods
     * =========================
     */
    /*
     * =========================
     * Game logic
     * =========================
     */

    /**
     * Uses the magic item on an enemy.
     *
     * @param hero  the hero using the item
     * @param e     the enemy being attacked
     * @param value damage dealt
     * @return false (handled by inventory logic if destroyed)
     */
    public boolean use_item(Hero hero, Enemy e, int value) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(e);

        if (hero.getMana() >= manaCost) {
            hero.setMana(hero.getMana() - manaCost);
            e.getAttacked(value);
            e.getAttacked(value);
        }
        return false;
    }

    /*
     * =========================
     * Factory / cloning
     * =========================
     */

    @Override
    public Item createNewInstance() {
        return new MagicItem(
                name,
                x,
                y,
                manaCost,
                rarity,
                damage,
                destroy,
                new ArrayList<>(effects),
                new ArrayList<>(occupiedCases),
                description);
    }

    /*
     * /
     * if (prob < 70) {
     * return new MagicItem(
     * "Cleansing Wand",
     * 2, 2, 0, prob, 6, false,
     * new ArrayList<>(List.of(new PoisonHero(2))),
     * twoWandCases(),
     * "Deals 6 Damage\n"+ " Poison Hero by 2\n"
     * );
     * }
     * 
     * if (prob < 90) {
     * return new MagicItem(
     * "Wizard Staff",
     * 1, 2, 1, prob, 15, false,
     * new ArrayList<>(List.of(new AddDamageAdjWeapon(2))),
     * twoBatonCases(),
     * "Deals 15 Damage\n Add 2 damage for adj weapons\n"
     * );
     * }
     * 
     * return new MagicItem(
     * "Apprentice Staff",
     * 1, 3, 1, prob, 20, false,
     * new ArrayList<>(List.of(new AttackAllEnemies(3))),
     * threeBatonCases(),
     * "Deals 20 Damage \n Attack all enemies with 3"
     * );
     */
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);

        if (this instanceof MagicItem wea &&
                (wea.name().equals("Cleansing Wand") || wea.name().equals("Wizard Staff"))) {

            for (Effect ef : wea.effects()) {
                ef.execute(hero, enemies, e, wea);

            }
        }
    }

    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        Objects.requireNonNull(e);
        if (this instanceof MagicItem wea) {
            use_item(hero, e, damage);
            for (Effect ef : wea.effects()) {
                if (ef instanceof AttackAllEnemies) {
                    ef.execute(hero, enemies, e, wea);
                }

            }
        }
    }

    public void baseShape() {
        if (this instanceof MagicItem magic) {
            if (magic.name.equals("Cleansing Wand")) {
                magic.setAttack(6);
            } else if (magic.name.equals("Wizard Staff")) {
                magic.setAttack(15);
            } else {
                magic.setAttack(20);
            }
        }
    }
}
