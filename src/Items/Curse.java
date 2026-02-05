package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import Enemies.Enemy;
import model.Hero;

import effects.Effect;

public class Curse implements Item {

    private String name;
    private String description;
    private ArrayList<Point> shape;
    private int damage;
    private int id;
    static int ID = 0;

    public Curse(String name) {
        this(name, "A dark energy taking the shape of an L.", new ArrayList<>(List.of(
                new Point(0, 0),
                new Point(0, 1),
                new Point(1, 1))));
    }

    public Curse(String name, String description, ArrayList<Point> shape) {
        this.name = name;
        this.description = description;
        this.shape = shape;
        this.damage = new Random().nextInt(1, 10);
        this.id = ID++;
    }

    @Override
    public ArrayList<Effect> effects() {
        return new ArrayList<>();
    }

    @Override
    public Item createNewInstance() {
        return new Curse(this.name, this.description, new ArrayList<>(this.shape));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public ArrayList<Point> occupiedCases() {
        return shape;
    }

    @Override
    public int x() {
        return heightItem();
    }

    @Override
    public int y() {
        return widthItem();
    }

    @Override
    public int rarity() {
        return 0; //
    }

    @Override
    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        // Curses typically cannot be "used"
        Objects.requireNonNull(hero);

        hero.setHp(hero.getHp() - damage);
        var p = hero.getBackpack().getItems().get(this).getFirst();
        hero.getBackpack().removeItem(p.x, p.y);
    }

    @Override
    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
        // Passive effect
    }

    @Override
    public void baseShape() {
        // Reset shape
    }

    @Override
    public List<Point> rotatePoints() {
        return shape;
    }
}
