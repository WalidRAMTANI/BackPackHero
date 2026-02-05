package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record AddEnergyCost(int n) implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		Objects.requireNonNull(hero);
		hero.setEnergy(hero.getEnergy() + n);
		return this;
	}
	
}
