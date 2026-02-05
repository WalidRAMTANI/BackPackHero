package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.AddProtectionAdj;
import effects.Effect;
import effects.NumberUses;
import model.Hero;

/**
 * Represents a Food item.
 * <p>
 * A Food item can be consumed by the Hero to apply effects
 * (healing, energy, etc.). Depending on its configuration,
 * it may be destroyed after use.
 * </p>
 */
public class Food implements Item {

    /* =========================
       Static fields
       ========================= */

    /**
     * Auto-incremented ID counter
     */
    public static int ID = 0;

    /* =========================
       Instance fields
       ========================= */

    private  int id;
    private  String name;
    private int x;
    private int y;

    private  int rarity;
    private  boolean destroy;
    private  ArrayList<Effect> effects;
    private  ArrayList<Point> occupiedCases;
    private  String description;

    /* =========================
       Constructors
       ========================= */

    /**
     * Full constructor.
     */
    public Food(int id,
                String name,
                int x,
                int y,
                int rarity,
                boolean destroy,
                ArrayList<Effect> effects,
                ArrayList<Point> occupiedCases,
                String description) {

        // Validation (equivalent to record compact constructor)
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases must not be null");

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.rarity = rarity;
        this.destroy = destroy;
        this.effects = effects;
        this.occupiedCases = occupiedCases;
        this.description = description;
    }

    /**
     * Constructor with automatic ID generation.
     */
    public Food(String name,
                int x,
                int y,
                int rarity,
                boolean destroy,
                ArrayList<Effect> effects,
                ArrayList<Point> occupiedCases,
                String description) {

        this(ID++, name, x, y, rarity, destroy, effects, occupiedCases, description);
    }

    /* =========================
       Getters (same names as fields)
       ========================= */

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

    public int rarity() {
        return rarity;
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

    /* =========================
       Game logic
       ========================= */

    /**
     * Uses the food item.
     * <p>
     * Food affects only the hero (heal, energy, etc.).
     * Whether the food disappears depends on {@code destroy}.
     * </p>
     *
     * @param hero the hero using the food
     * @param e enemy (unused)
     * @param gainValue value gained (heal, energy, etc.)
     * @return true if the food was actually used
     */
    public boolean use_item(Hero hero, Enemy e, int gainValue) {
        Objects.requireNonNull(hero);
        boolean used = false;

        // Logic commented out intentionally (same as original code)
        // Add concrete behavior here when effects are implemented

        return used;
    }

    /* =========================
       Factory / cloning
       ========================= */

    /**
     * Creates a new independent instance of this Food.
     */
    @Override
    public Item createNewInstance() {
        return new Food(
                name,
                x,
                y,
                rarity,
                destroy,
                new ArrayList<>(effects),
                new ArrayList<>(occupiedCases),
                description
        );
    }
    /*
                    new ArrayList<>(List.of(new AddHpHero(2), new NumberUses(1))),
                    "Adds 2 HP");
                    new ArrayList<>(List.of(new AddEnergyCost(2), new NumberUses(1))),
                    "Adds 2 Energy");
                    new ArrayList<>(List.of(new AddEnergyCost(3), new NumberUses(1))),
                    twoFoodCases(),
                    "Adds 3 Energy");
        };*/
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);

        if (this instanceof Food food &&
            food.name().equals("Steak")) {

            for (Effect ef : food.effects()) {
                System.out.println("description : " + food.description());
                if(ef instanceof AddProtectionAdj) {
                	ef.execute(hero, enemies, e, food);
                }
                
            }
        }
    }
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        boolean del = false;
        if (this instanceof Food food) {
        	for (Effect ef : food.effects()) {
                System.out.println("description : " + food.description());
                if((ef instanceof AddProtectionAdj) == false) {
                	ef.execute(hero, enemies, e, food);
                }
                if(ef instanceof NumberUses eff) {
                	if(eff.n() <= 0) {
                		del = true;
                	}
                }
                
            }
        	if(del) {
        		var p = hero.getBackpack().getItems().get(this).getFirst();
        		hero.getBackpack().removeItem(p.x, p.y);
        		System.out.println("Item removed");
        	}
        }
    }
    public void baseShape() {
    	
    }
}
