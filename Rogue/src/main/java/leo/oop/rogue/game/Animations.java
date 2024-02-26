package leo.oop.rogue.game;

import java.util.ArrayList;
import java.util.Map.Entry;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public class Animations extends GameElement{

	public Animations(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		Hero h = GameEngine.getInstance().getHero();

		// Se houver um adversário que esteja a atacar
		for(Entry<Integer,ArrayList<ImageTile>> entry : GameEngine.getInstance().getRooms().entrySet())
			for(ImageTile i : entry.getValue())
				if(i instanceof Movable && !i.equals(h) && ((Movable) i).enemyAttack){
					if(i instanceof Scorpio)
						return "PoisonAttack1";
					return "EnemyAttack_1";
				}

		if(h.states.contains(States.HAS_SWORD) && !h.states.contains(States.HEALING))
			return "Attack_3";
		if(h.states.contains(States.HEALING))
			return "Healing_1";
		
		return "Attack_2";
	}

	@Override
	public int getLayer() {
		return 3;
	}

}
