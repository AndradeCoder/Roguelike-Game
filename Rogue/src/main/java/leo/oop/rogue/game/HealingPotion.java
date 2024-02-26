package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class HealingPotion extends GameElement implements Items{

	public HealingPotion(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return "HealingPotion";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void use(String s) {}

	@Override
	public void drop(String s, Point2D p, boolean testeAdicional) {
		Hero h = GameEngine.getInstance().getHero();
		
		if(s.equals("Drop") && !testeAdicional){
			try {
				GameEngine.getInstance().removeObject(this);
				
				h.states.add(States.HEALING);
				h.states.remove(States.POISENED);
				Animations ei = new Animations(h.position);
				if( h.hp+5 > 10)
					h.hp = 10;
				else h.hp +=5;

				GameEngine.getInstance().addObject(ei);
				Thread.sleep(500);
				
				GameEngine.getInstance().removeObject(ei);
				h.states.remove(States.HEALING);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(!s.equals("Drop")){
			this.position=p;
			h.inventory.remove(this);
		}
	}

}
