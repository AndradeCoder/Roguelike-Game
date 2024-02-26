package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Scorpio extends Movable{

	public Scorpio(Point2D position) {
		super(position);
		hp=2;
	}
	
	@Override
	public void attack(Movable m){
		super.attack(m);
		GameEngine.getInstance().getHero().states.add(States.POISENED);
	}

	@Override
	public String getName() {
		return "Scorpio";
	}

	@Override
	public int getLayer() {
		return 2;
	}

}
