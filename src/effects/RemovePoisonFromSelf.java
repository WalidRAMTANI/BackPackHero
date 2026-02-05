package effects;

import java.util.ArrayList;
import java.util.Objects;

import Enemies.Enemy;
import Items.Item;
import model.Hero;

public record RemovePoisonFromSelf() implements Effect{

	@Override
	public Effect execute(Hero hero, ArrayList<Enemy> enemies, Enemy e, Item item) {
		// TODO Auto-generated method stub
		Objects.requireNonNull(hero);
		Objects.requireNonNull(item);
		var opt = hero.getEffects().stream().filter(el -> el.getClass().equals("PoisonHero")).findFirst();
		if(opt.isPresent()) {
			hero.getEffects().remove((PoisonHero)opt.get());
		}
		return null;
	} 

}
