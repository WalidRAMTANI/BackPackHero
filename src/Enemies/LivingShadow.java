package Enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import effects.Curse;
import effects.Effect;
import model.Hero;

public class LivingShadow implements Enemy {

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
    public LivingShadow(int id,
            int hpMax,
            int hp,
            int attack,
            int defense,
            int xp,
            String description,
            ArrayList<Effect> effects,
            HashSet<Integer> actions) {

        this.id = id;
        this.hpMax = hpMax;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.xp = xp;
        this.description = description;
        this.actions = Objects.requireNonNull(actions, "actions cannot be null");
        this.effects = effects;
        if (hp < 0)
            throw new IllegalArgumentException("hp < 0");
        if (hpMax < 0)
            throw new IllegalArgumentException("hpMax < 0");
        if (attack < 0)
            throw new IllegalArgumentException("attack < 0");
        if (defense < 0)
            throw new IllegalArgumentException("defense < 0");
    }

    // Constructeur simplifié
    public LivingShadow() {
        this(ID++, 50, 50, 0, 0, 25, "A dark shadow of yourself.\n Doppel-get-over-it!\"\n he can curse you\n",
                generateEffects(), new HashSet<>());
    }

    public void setHp(int amount) {
        hp = Math.min(hpMax, amount + hp);
    }

    private static ArrayList<Effect> generateEffects() {
        var l = new ArrayList<Effect>();
        l.add(new Curse());
        return l;
    }

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

    private LivingShadow weaponAttacked(int effectiveDamage) {
        int newHp = Math.max(0, hp - effectiveDamage);
        this.hp = newHp;
        return this;
    }

    private LivingShadow weaponAttacked(int effectiveDamage, int defence) {
        int newHp = Math.max(0, hp - Math.max(0, effectiveDamage - defence));
        this.hp = newHp;
        return this;
    }

    // --- Getters ---

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

    @Override
    public String toString() {
        return "OmbreVivante [HP=" + hp + "/" + hpMax +
                ", Attack=" + attack + ", Defense=" + defense + "]";
    }

    @Override
    public void setActions(HashSet<Integer> h) {
        Objects.requireNonNull(h);
        actions = new HashSet<>(h);
    }

    @Override
    public void setAttack(int i) {
        attack += i;

    }

    public void apply(Hero hero, ArrayList<Enemy> enemies) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(enemies);
        for (var eff : effects) {
            eff.execute(hero, enemies, this, null);
        }
    }

    public void baseShape() {
        this.attack = 0;
    }
}
