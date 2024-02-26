package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Wall extends GameElement{

	public Wall(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		switch (GameEngine.getInstance().getCurrentLevel()) {
		case 0: return "Wall5";
		case 1: return "Floor_10";
		case 2: return "Wall2";
		case 3: return "Wall3";
		}
		return null;
	}

	@Override
	public int getLayer() {
		return 0;
	}

}
