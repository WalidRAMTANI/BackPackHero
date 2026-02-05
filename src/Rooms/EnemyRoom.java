package Rooms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import Enemies.Enemy;
import Items.Item;
import model.Hero;

/**
 * Represents a room where enemies appear and drops rewards upon clearing.
 */
public class EnemyRoom implements RoomType {

    private final ArrayList<Enemy> enemies;
    private final List<Item> rewards;
    private boolean isCleared;

    /**
     * @param enemies List of enemies in the room
     * @param rewards List of rewards dropped by enemies
     */
    public EnemyRoom(ArrayList<Enemy> enemies, List<Item> rewards) {
        Objects.requireNonNull(enemies, "enemies cannot be null");
        Objects.requireNonNull(rewards, "rewards cannot be null");
        this.enemies = enemies;
        this.rewards = rewards;
        this.isCleared = false;
    }

    /**
     * Default constructor for EnemyRoom.
     */
    public EnemyRoom() {
        this(new ArrayList<>(Enemy.generateEnmies()), new ArrayList<>(Item.generateItems()));
    }

    @Override
    public String getTypeName() {
        return "ENEMY";
    }

    /**
     * @return List of enemies
     */
    public ArrayList<Enemy> enemies() {
        return enemies;
    }

    /**
     * @return List of rewards
     */
    public List<Item> rewards() {
        return rewards;
    }

    /**
     * @return true if room is cleared
     */
    public boolean isCleared() {
        return isCleared;
    }

    /**
     * @param cleared Set the cleared state
     */
    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    /**
     * @param l New list of enemies
     * @return Updated list of enemies
     */
    public ArrayList<Enemy> setEnnemies(ArrayList<Enemy> l) {
        Objects.requireNonNull(l, "l cannot be null");
        enemies.clear();
        enemies.addAll(l);
        return enemies;
    }

    /**
     * @param e          The enemy to set actions for
     * @param maxActions Maximum number of actions
     */
    public void chooseActionEnemies(Enemy e, int maxActions) {
        Objects.requireNonNull(e, "e cannot be null");
        Random rand = new Random();
        HashSet<Integer> newActions = new HashSet<>();
        int nActions = rand.nextInt(maxActions);
        for (int i = 0; i < nActions; i++) {
            int action = rand.nextInt(3) + 1;
            newActions.add(action);
        }
        e.setActions(newActions);
    }

    /**
     * Executes enemies' turn.
     * 
     * @param hero Hero reference
     */
    public void enemiesTurn(Hero hero) {
        Objects.requireNonNull(hero);
        enemies.forEach(e -> {
            for (int action : e.actions()) {
                switch (action) {
                    case 1 -> hero.takeDamage(e);
                    case 3 -> e.apply(hero, enemies);
                    default -> {
                    }
                }
            }
        });
        hero.setProtection(hero.getDefence());
        hero.setEnergy(3);
        hero.setMana(2);
    }
}
