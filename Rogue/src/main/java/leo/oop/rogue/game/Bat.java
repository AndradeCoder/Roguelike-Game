package leo.oop.rogue.game;

import java.util.List;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Bat extends Movable{

	private int timesHealed = 0;

	public Bat(Point2D position) {
		super(position);
		hp=3;
		damage=1;
	}

	@Override
	public String getName() {
		return "Bat";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public void attack(Movable m){
		int heroHP = GameEngine.getInstance().getHero().hp;
		if(Math.random() < 0.5){
			if(timesHealed == 3)
				super.attack(m);
			else{
				super.attack(m);
				if(heroHP != GameEngine.getInstance().getHero().hp){
					this.hp++;
					timesHealed++;
				}
			}
		}
	}

	@Override
	public void move(){
		this.dir = Direction.random();
		Vector2D vec = this.dir.asVector();
		Point2D newPos = position.plus(vec);
		List<GameElement> ge = GameEngine.getInstance().getObjects(newPos);

		if(Math.random() < 0.5){
			if(canMove(position.plus(vec)) || (ge.size()>=2 && ge.get(1) instanceof Items))
				position = newPos;
		} else super.move();
	}

}
