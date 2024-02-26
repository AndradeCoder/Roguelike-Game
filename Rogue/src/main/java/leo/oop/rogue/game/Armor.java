package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Armor extends GameElement implements Items{

	public Armor(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return "Armor2";	// 1 ou 2?
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void use(String s) {
		Hero h = GameEngine.getInstance().getHero();
		if(s.equals("Use") && !h.states.contains(States.HAS_ARMOR))
			h.states.add(States.HAS_ARMOR);
	}

	@Override
	public void drop(String s, Point2D p, boolean testeAdicional) {
		Hero h = GameEngine.getInstance().getHero();
		if(s.equals("Drop")){
			if(nrOfItemsFromSameType(this)==1)		// Se for a unica peça de armadura do inventário vai retirar os efeitos de ter armadura
				h.states.remove(States.HAS_ARMOR);
		}
		else{
			this.position = p;
			h.inventory.remove(this);
		}
	}

}
