package Enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import effects.Effect;
import effects.HeallAllEnemies;
import effects.PoisonHero;
import model.Hero;

public class QueenBee implements Enemy {

    private int id;
    private int hpMax;
    private int hp;
    private int attack;
    private int defense;
    private String description;
    private int xp;
    private ArrayList<Effect> effects;
    private HashSet<Integer> actions;

    public static int ID = 0;

    // Constructeur principal
    public QueenBee(int id, int hpMax, int hp, int attack, int defense, int xp, String description,
            ArrayList<Effect> effects, HashSet<Integer> actions) {
        this.id = id;
        this.hpMax = hpMax;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.xp = xp;
        this.effects = effects;

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

    // Constructeur simplifié sans actions
    public QueenBee() {
        this(ID++, 74, 74, 15, 0, 20, "The queen bee heal all armies by 5,\n poison hero by 1\n!", generateEffects(),
                new HashSet<>());
    }

    private static ArrayList<Effect> generateEffects() {
        var l = new ArrayList<Effect>();
        l.add(new HeallAllEnemies(5));
        l.add(new PoisonHero(1));
        return l;
    }

    public void setHp(int amount) {
        hp = Math.min(hpMax, amount + hp);
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
        return actions;
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
        return "ReineAbeille [HP=" + hp + "/" + hpMax +
                ", Attack=" + attack +
                ", Defense=" + defense + "]";
    }

    @Override
    public void setAttack(int i) {
        attack += i;

    }

    @Override
    public void setActions(HashSet<Integer> h) {
        Objects.requireNonNull(h);
        actions = new HashSet<>(h);
    }

    public void apply(Hero hero, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        for (var eff : effects) {
            eff.execute(hero, enemies, this, null);
        }
    }

    public void baseShape() {
        this.attack = 15;
    }
}
