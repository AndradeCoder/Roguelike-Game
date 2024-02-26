package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public interface Items {
	void use(String s);
	void drop(String s, Point2D p, boolean testeAdicional);
	
	default int nrOfItemsFromSameType(Object o) {
		Hero h = GameEngine.getInstance().getHero();
		int numberOfItems = 0;
		for(Items item : h.inventory)
			if(item.getClass().isInstance(o))
				numberOfItems++;
		return numberOfItems;
	}
}
