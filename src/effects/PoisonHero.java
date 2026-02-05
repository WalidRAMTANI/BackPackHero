package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record PoisonHero(int malus) implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		hero.setHp(hero.getHp() - malus);
		return null;
	}

}
