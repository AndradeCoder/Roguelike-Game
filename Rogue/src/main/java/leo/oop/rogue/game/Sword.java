package leo.oop.rogue.game;

import pt.iscte.poo.utils.Point2D;

public class Sword extends GameElement implements Items{

	public Sword(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return "Sword12";	//Nota Pessoal: 1,4,6 ou 12
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void use(String s) {		// Apenas duplica o dano 1 vez, independentemente de quantas espadas tenha no inventário
		Hero h = GameEngine.getInstance().getHero();
		if(s.equals("Use") && !h.states.contains(States.HAS_SWORD)){
			h.states.add(States.HAS_SWORD);
			h.damage *= 2;
			System.out.println("Hero Damage:  "+h.damage);
		}
	}

	@Override
	public void drop(String s, Point2D p, boolean testeAdicional) {
		Hero h = GameEngine.getInstance().getHero();
		if(s.equals("Drop")){
			if(nrOfItemsFromSameType(this)==1){		// Se for a unica espada do inventário vai retirar os efeitos de ter uma espada
				h.states.remove(States.HAS_SWORD);
				h.damage /= 2;
				System.out.println("Hero Damage:  "+h.damage);
			}
		}
		else{
			this.position = p;
			h.inventory.remove(this);
		}
	}

}
