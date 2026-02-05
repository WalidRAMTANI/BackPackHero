package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record ReduceDamageEffectEnemy(int malus) implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(e);
		Objects.requireNonNull(item);
		e.setAttack(e.attack() - malus);
		return null;
	}
	
}
