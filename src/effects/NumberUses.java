package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public class NumberUses implements Effect{
	private int n;
	public NumberUses(int i){
		if(i < 0) {
			throw new IllegalArgumentException("i cant be < 0");
		}
		this.n = i;
	}
	@Override
	public NumberUses execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(item);
		if(n > 0) {
			n = n-1;
		}
		return this;
	}
	public int n() {
		return n;
	}
}
