package Enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import effects.Effect;
import effects.HeallAllEnemies;
import effects.PoisonHero;
import model.Hero;

public class FrogWizard implements Enemy {

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
    public FrogWizard(int id, int hpMax, int hp, int attack, int defense, int xp, String description,
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

    // Constructeur simplifié sans actions
    public FrogWizard() {
        this(ID++, 45, 45, 10, 0, 16,
                "Frogs know about a deep magic that appears on rare Wednesdays\n He can poison the hero by 4 ,\n Heal all allies by +10\n",
                generateEffects(), new HashSet<>());
    }

    private static ArrayList<Effect> generateEffects() {
        var l = new ArrayList<Effect>();
        l.add(new HeallAllEnemies(10));
        l.add(new PoisonHero(4));
        return l;
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

    public void setHp(int amount) {
        hp = Math.min(hpMax, amount + hp);
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
        return "SorciereGrenouille [HP=" + hp + "/" + hpMax +
                ", Attack=" + attack +
                ", Defense=" + defense + "]";
    }

    @Override
    public void setAttack(int i) {
        attack += i;

    }

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
        this.attack = 10;
    }
}
