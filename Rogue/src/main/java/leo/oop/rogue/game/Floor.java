package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Floor extends GameElement {


	public Floor(Point2D position) {
		super(position);
		isPassable=true;
	}

	@Override
	public String getName() {
		switch (GameEngine.getInstance().getCurrentLevel()) {
		case 0: return "Floor_3";
		case 1: return "Floor_6";
		case 2: return "Floor_14";
		case 3: return "Floor_17";
		}
		return null;
	}

	@Override
	public int getLayer() {
		return 0;
	}

}