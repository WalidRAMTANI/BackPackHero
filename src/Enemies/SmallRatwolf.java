package Enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import effects.Effect;
import model.Hero;

public class SmallRatwolf implements Enemy {

    private int id;
    private int hpMax;
    private int hp;
    private int attack;
    private int defense;
    private int xp;
    private String description;
    private ArrayList<Effect> effects;
    private HashSet<Integer> actions;
    public static int ID = 0;

    // Constructeur principal
    public SmallRatwolf(int id, int hpMax, int hp, int attack, int defense, int xp, String description,
            ArrayList<Effect> effects, HashSet<Integer> actions) {
        this.id = id;
        this.hpMax = hpMax;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.xp = xp;
        this.effects = effects;
        this.description = description;
        this.actions = Objects.requireNonNull(actions, "actions cannot be null");
        if (hp < 0)
            throw new IllegalArgumentException("hp < 0");
        if (hpMax < 0)
            throw new IllegalArgumentException("hpMax < 0");
        if (attack < 0)
            throw new IllegalArgumentException("attack < 0");
        if (defense < 0)
            throw new IllegalArgumentException("defense < 0");
    }

    public void setHp(int amount) {
        hp = Math.min(hpMax, amount + hp);
    }

    // Constructeur simplifié sans actions
    public SmallRatwolf() {
        this(ID++, 32, 32, 9, 14, 6,
                "\"The smaller version of the ratwolves\n that patrol the upper layers of the dungeon\"",
                new ArrayList<Effect>(), new HashSet<>());
    }

    // Getters (style record)
    public int id() {
        return id;
    }

    public int hpMax() {
        return hpMax;
    }

    public int hp() {
        return hp;
    }

    public int attack() {
        return attack;
    }

    public int defense() {
        return defense;
    }

    public int exp() {
        return xp;
    }

    public HashSet<Integer> actions() {
        return actions; // copie défensive
    }

    public String description() {
        return description;
    }

    // Gameplay

    @Override
    public void execute(Hero hero) {
        Objects.requireNonNull(hero);
        if (actions.contains(3)) {
            for (var effect : effects) {
                effect.execute(hero, null, this, null);
            }
        }
        if (actions.contains(1)) {
            hero.takeDamage(this);
        }
    }

    public Enemy getAttacked(int effectiveDamage) {
        if (actions.contains(2)) {
            return defend(effectiveDamage);
        }
        return weaponAttacked(effectiveDamage);
    }

    public Enemy defend(int effectiveDamage) {
        return weaponAttacked(effectiveDamage, defense);
    }

    private Enemy weaponAttacked(int effectiveDamage) {
        this.hp = Math.max(0, hp - effectiveDamage);
        return this;
    }

    private Enemy weaponAttacked(int effectiveDamage, int defence) {
        this.hp = Math.max(0, hp - Math.max(0, effectiveDamage - defence));
        return this;
    }

    @Override
    public String toString() {
        return "PetitRatLoup [HP=" + hp + "/" + hpMax +
                ", Attack=" + attack +
                ", Defense=" + defense +
                "]";
    }

    @Override
    public void setAttack(int i) {
        attack += i;

    }

    @Override
    public void setActions(HashSet<Integer> h) {
        Objects.requireNonNull(h);
        actions = new HashSet(h);
    }

    @Override
    public void apply(Hero hero, ArrayList<Enemy> enemies) {
        // TODO Auto-generated method stub
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);

    }

    @Override
    public void baseShape() {
        this.attack = 9;
    }
}
