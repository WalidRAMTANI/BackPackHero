package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.AttackAllEnemies;
import effects.Effect;
import model.Hero;

/**
 * Represents a ranged weapon.
 * <p>
 * A RangedWeapon consumes energy to deal damage to an enemy from distance.
 * It may be destroyed after use depending on its configuration.
 * </p>
 */
public class RangedWeapon implements Weapon, Item {

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

    private  int energyCost;
    private  int rarity;
    private  int damage;
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
    public RangedWeapon(int id,
                        String name,
                        int x,
                        int y,
                        int energyCost,
                        int rarity,
                        int damage,
                        boolean destroy,
                        ArrayList<Effect> effects,
                        ArrayList<Point> occupiedCases,
                        String description) {

        // Validation (equivalent to record compact constructor)
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(occupiedCases, "occupiedCases cannot be null");

        if (energyCost < 0) {
            throw new IllegalArgumentException("energyCost < 0");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("damage < 0");
        }

        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.energyCost = energyCost;
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
    public RangedWeapon(String name,
                        int x,
                        int y,
                        int energyCost,
                        int rarity,
                        int damage,
                        boolean destroy,
                        ArrayList<Effect> effects,
                        ArrayList<Point> occupiedCases,
                        String description) {

        this(ID++, name, x, y, energyCost, rarity, damage,
             destroy, effects, occupiedCases, description);
    }
    public void setAttack(int atta) {
    	damage += atta;
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

    public int energyCost() {
        return energyCost;
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

    /* =========================
       Game logic
       ========================= */

    /**
     * Uses the ranged weapon.
     *
     * @param hero the hero using the weapon
     * @param e the enemy being attacked
     * @param value damage dealt
     * @return true if the attack was performed
     */
    private boolean use_item(Hero hero, Enemy e, int value) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(e);

        if (hero.getEnergy() >= energyCost) {
            hero.setEnergy(hero.getEnergy() - energyCost);
            e.getAttacked(value);
            System.out.println("Used ranged weapon " + name + ", energy -" + energyCost);
            return true;
        }
        return false;
    }

    /* =========================
       Factory / cloning
       ========================= */

    @Override
    public Item createNewInstance() {
        return new RangedWeapon(
                name,
                x,
                y,
                energyCost,
                rarity,
                damage,
                destroy,
                new ArrayList<>(effects),
                new ArrayList<>(occupiedCases),
                description
        );
    }
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        if (this instanceof RangedWeapon wea &&
        		(wea.name().equals("Shiv"))) {

            for (Effect ef : wea.effects()) {
                System.out.println("description : " + wea.description());
               	ef.execute(hero, enemies, e, wea);
                
            }
        }
    }
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
		// TODO Auto-generated method stub
		
		Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        Objects.requireNonNull(e);
        if (this instanceof RangedWeapon wea) {
        	System.out.println("Attack enemy");
        	use_item(hero, e, damage);
        	if(wea.name.equals("Hachet")) {
        		for (Effect ef : wea.effects()) {
                    
                    	ef.execute(hero, enemies, e, wea);
                    
                }
        	}
            
        }
	}
    public void baseShape() {
    	if(this instanceof RangedWeapon wea) {
    		if(wea.name.equals("Crossbow")) {
    			wea.setAttack(10);
    		}else if(wea.name.equals("Shiv")) {
    			wea.setAttack(15);
    		}else {
    			wea.setAttack(20);
    		}
    	}
    }
}
