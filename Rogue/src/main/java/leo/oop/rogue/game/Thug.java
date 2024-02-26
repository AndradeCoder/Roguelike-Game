package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Thug extends Movable{


	public Thug(Point2D position) {
		super(position);
		hp=10;
		damage=3;
	}

	@Override
	public String getName() {
		return "Thug";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public void attack(Movable m){
		if(Math.random() < 0.3)
			super.attack(m);
	}


}
