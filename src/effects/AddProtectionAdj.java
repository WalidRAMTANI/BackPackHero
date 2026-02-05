package effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Enemies.Enemy;
import Items.Armor;
import Items.Item;
import model.Hero;

public record AddProtectionAdj(int value) implements Effect {

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(item);

		List<Item> adj_items = hero.getBackpack().getAdjacentItems(item);
		for (var p : adj_items) {
			if (p.getClass().equals("Armor") && p instanceof Armor eq) {
				eq.setProtection(eq.protectionValue() + value);
			}
		}

		return this;
	}

}
