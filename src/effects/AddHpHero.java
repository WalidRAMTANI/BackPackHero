package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record AddHpHero(int hp) implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		Objects.requireNonNull(hero);
		hero.setHp(hero.getHp() + hp);
		return this;
	}

}
