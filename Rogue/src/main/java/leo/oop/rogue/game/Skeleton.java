package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Skeleton extends Movable{
	
	public Skeleton(Point2D position) {
		super(position);
		hp=5;
		damage=1;
	}

	@Override
	public String getName() {
		return "Skeleton";
	}

	@Override
	public int getLayer() {
		return 2;
	}

}
