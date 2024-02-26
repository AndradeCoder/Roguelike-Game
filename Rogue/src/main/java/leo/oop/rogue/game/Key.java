package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Key extends GameElement implements Items{

	protected final String id;

	public Key(Point2D position, String id) {
		super(position);
		this.id=id;
	}


	@Override
	public String getName() {
		return "Key";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void use(String s) {}

	@Override
	public void drop(String s, Point2D p, boolean testeAdicional) {
		if(!s.equals("Drop")){
			this.position = p;
			GameEngine.getInstance().getHero().inventory.remove(this);
		}
	}

}
