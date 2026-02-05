package Items;

import java.awt.Point;
import java.util.ArrayList;

import Enemies.Enemy;
import effects.Effect;
import model.Hero;

public record Key(
        int id,
        String name,
        int x, int y,
        int rarity,
        ArrayList<Point> occupiedCases) implements Item {

    public String description() {
        return "A key that might open something.";
    }

    public static int ID = 0;

    public Key(String name, int rarity) {
        this(ID++, name, 1, 1, rarity, new ArrayList<>() {
            {
                add(new Point(0, 0));
            }
        });
    }

    // @Override
    public boolean use_item(Hero hero, Enemy e, int value) {
        // Key is NO T used on hero
        return false;
    }

    @Override
    public Item createNewInstance() {
        return new Key(name, rarity);
    }

    @Override
    public ArrayList<Effect> effects() {
        // TODO Auto-generated method stub
        return null;
    }

    public void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {

    }

    public void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies) {
    }

}
