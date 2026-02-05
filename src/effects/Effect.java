package effects;

import java.util.ArrayList;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public interface Effect {
	Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item);
}
