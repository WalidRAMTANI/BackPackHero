package effects;

import java.util.ArrayList;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record AddDamageIfCurseAdjDiago(int damage) implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		return null;
	}

}
