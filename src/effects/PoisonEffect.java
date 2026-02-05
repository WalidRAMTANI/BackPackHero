package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record PoisonEffect(int damage) implements Effect{
	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemies);
		enemies.forEach(en -> en.setHp(en.hp() - damage));
		return null;
	}
}
