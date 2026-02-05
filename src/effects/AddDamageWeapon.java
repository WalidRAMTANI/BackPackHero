package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import Items.Weapon;
import model.Hero;

public class AddDamageWeapon implements Effect{
	private int damage;
	private int duration;
	public AddDamageWeapon(int damage) {
		this.damage = damage;
		this.duration = 1;
	}

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(item);
		if(item instanceof Weapon wea && duration > 0) {
			wea.setAttack(wea.damage() + damage);
			duration --;
		}else {
			item.effects().remove(this);
		}
		return null;
	}
}
