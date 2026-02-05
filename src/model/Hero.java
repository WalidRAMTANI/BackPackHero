package model;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import effects.Effect;

/**
 * Represents the player character.
 */
public class Hero {
    private String name;
    private int hpMax;
    private int hp;
    private int energy;
    private int mana;
    private int level;
    private int xp;
    private int defence;
    private int protection;
    private ArrayList<Effect> effects;
    private BackPack backpack;
    private int pendingCurses = 0;
    private int cursesReceivedInBattle = 0;

    /**
     * @param name       Hero name
     * @param hpMax      Max health
     * @param hp         Current health
     * @param energy     Current energy
     * @param mana       Current mana
     * @param level      Current level
     * @param xp         Current XP
     * @param defence    Base defence
     * @param protection Current protection
     */
    public Hero(String name, int hpMax, int hp, int energy, int mana, int level, int xp, int defence, int protection) {
        Objects.requireNonNull(name, "name cannot be null");
        if (hpMax < 0 || defence < 0 || hp < 0 || energy < 0 || mana < 0 || level < 0 || xp < 0 || protection < 0) {
            throw new IllegalArgumentException("Invalid attributes");
        }

        this.name = name;
        this.hpMax = hpMax;
        this.hp = hp;
        this.energy = energy;
        this.mana = mana;
        this.level = level;
        this.xp = xp;
        this.defence = defence;
        this.protection = protection;
        this.backpack = new BackPack();
        this.effects = new ArrayList<Effect>();
    }

    /**
     * @param name    Hero name
     * @param defence Initial defence
     */
    public Hero(String name, int defence) {
        this(name, 40, 40, 3, 2, 0, 0, defence, 0);
    }

    /** @return Hero name */
    public String getName() {
        return name;
    }

    /** @return Max HP */
    public int getHpMax() {
        return hpMax;
    }

    /** @return Current HP */
    public int getHp() {
        return hp;
    }

    /** @return Current energy */
    public int getEnergy() {
        return energy;
    }

    /** @return Current mana */
    public int getMana() {
        return mana;
    }

    /** @return Current level */
    public int getLevel() {
        return level;
    }

    /** @return Current XP */
    public int getXp() {
        return xp;
    }

    /** @return Base defence */
    public int getDefence() {
        return defence;
    }

    /** @return Current protection */
    public int getProtection() {
        return protection;
    }

    /** @return Reference to backpack */
    public BackPack getBackpack() {
        return backpack;
    }

    /** @return Active effects list */
    public ArrayList<Effect> getEffects() {
        return effects;
    }

    /** @param amount New energy level */
    public void setEnergy(int amount) {
        energy = Math.min(amount, 3);
    }

    /** @param amount New mana level */
    public void setMana(int amount) {
        mana = Math.min(amount, 2);
    }

    /** @param amount New protection level */
    public void setProtection(int amount) {
        protection = amount;
    }

    /**
     * @param amount New HP value
     * @return true if health increased
     */
    public boolean setHp(int amount) {
        boolean healing = hp < amount;
        hp = Math.max(0, Math.min(amount, hpMax));
        return healing;
    }

    /** @param backpack New backpack reference */
    public void setBackpack(BackPack backpack) {
        Objects.requireNonNull(backpack, "backpack cannot be null");
        this.backpack = backpack;
    }

    /**
     * Reduces HP based on enemy attack and current protection.
     * 
     * @param e Attacking enemy
     */
    public void takeDamage(Enemy e) {
        Objects.requireNonNull(e);
        int d = Math.max(0, e.attack() - getProtection());
        setHp(hp - d);
    }

    /** @param value XP amount to add */
    public void gainXP(int value) {
        if (value < 0)
            throw new IllegalArgumentException("XP cannot be negative");
        xp += value;
    }

    /** Handles level up logic and stat increases. */
    public void levelUp() {
        int xpThreshold = 50;
        while (xp >= level * xpThreshold) {
            xp -= level * xpThreshold;
            level++;
            hpMax += 10;
            setEnergy(energy + 2);
            setMana(mana + 2);
        }
    }

    /** @return true if HP is 0 or less */
    public boolean isDied() {
        return hp <= 0;
    }

    /** Marks a curse to be received. */
    public void applyCurse() {
        this.pendingCurses++;
        this.cursesReceivedInBattle++;
    }

    /** @return true if a curse was popped */
    public boolean popCurse() {
        if (pendingCurses > 0) {
            pendingCurses--;
            return true;
        }
        return false;
    }

    /** @return Count of curses received in current battle */
    public int getCursesReceivedInBattle() {
        return cursesReceivedInBattle;
    }

    /** Resets the battle curse counter. */
    public void resetCursesReceivedInBattle() {
        this.cursesReceivedInBattle = 0;
    }

    @Override
    public String toString() {
        return "Hero{name='" + name + "', hp=" + hp + "/" + hpMax + "}";
    }
}
