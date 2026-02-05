package Enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import model.Hero;

// Interface defining the behavior and attributes of an enemy
public interface Enemy {
    // Current health points
    int hp();

    // Experience points awarded when defeated
    int exp();

    String description();

    void apply(Hero hero, ArrayList<Enemy> enemies);

    void baseShape();

    // Maximum health points
    int hpMax();

    // Defensive stat, reduces incoming damage
    int defense();

    // Attack power stat
    int attack();

    // setHp
    void setHp(int amount);

    // Enemy attacks a given hero
    void execute(Hero hero);

    // Unique identifier for the enemy
    int id();

    void setAttack(int attack);

    // Set of possible actions the enemy can perform
    HashSet<Integer> actions();

    void setActions(HashSet<Integer> h);
    // Set of previous actions performed by the enemy
    // HashSet<Integer> previousAction();

    // Returns a new Enemy instance reflecting damage from being attacked by an item
    Enemy getAttacked(int effectiveDamage);

    // Enemy defends against an item, possibly modifying state or damage taken
    Enemy defend(int effectiveDamage);

    // Default method to check if the enemy is dead (hp <= 0)
    default boolean isDead() {
        return hp() <= 0;
    }

    // Sets or updates the previous action (method signature; implementation
    // elsewhere)
    // Enemy setPeviousAction();

    // Generate Random Enemies
    static List<Enemy> generateEnmies() {
        Random random = new Random();
        ArrayList<Enemy> lst = new ArrayList<>();
        int size = random.nextInt(3) + 1;
        for (int i = 0; i < size; i++) {
            int choice = random.nextInt(5);
            Enemy e = switch (choice) {
                case 0 -> new Ratwolf();
                case 1 -> new SmallRatwolf();
                case 2 -> new FrogWizard();
                case 3 -> new QueenBee();
                default -> new LivingShadow();
            };
            lst.add(e);
        }
        return List.copyOf(lst);
    }
}
