package leo.oop.rogue.game;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class Movable extends GameElement{

	protected List<States> states = new ArrayList<>();
	protected List<Items> inventory = new ArrayList<>();
	protected int hp;
	protected int damage;
	protected boolean enemyAttack;	// Usado nas animações
	protected Direction dir;

	public Movable(Point2D position) {
		super(position);
	}


	public void move(){		// Move dos adversários
		Vector2D vector = Vector2D.movementVector(this.position,GameEngine.getInstance().getHero().position);
		Point2D newPos = position.plus(vector);
		List<GameElement> ge = GameEngine.getInstance().getObjects(newPos);		
		if(canMove(newPos) || (ge.size()>=2 && ge.get(1) instanceof Items))	// 2ª condição: Adversários podem passar por cima de items
			position = newPos;

		dead(this);	// Quando morre sem ter sido diretamente atacado
	}

	public boolean canMove(Point2D pos){
		List<GameElement> ge = GameEngine.getInstance().getObjects(pos);
		
		if(ge.size()==1 && ge.get(0).isPassable)   // ge.size()==1 confirma que apenas tem um elemento na posição, o Floor 
			return true;
		if(!this.getName().contains("Hero") && ge.get(0) instanceof Hero){
			Hero hero = GameEngine.getInstance().getHero();

			if(hero.states.contains(States.HAS_ARMOR)){
				int beforeAttack = hero.hp;				
				attack(hero);
				if(Math.random() < 0.5 && hero.hp != beforeAttack)
					hero.hp+=this.damage;
			} else attack(hero);
		}
		return false;
	}

	public void attack(Movable m){
		Hero hero = GameEngine.getInstance().getHero();
		try {
			int old_EnemyHP = m.hp;

			if(!this.getName().equals(hero.getName()))
				enemyAttack = true;
			else hero.totalDamageDealt += this.damage;

			Animations ei = new Animations(m.position);
			GameEngine.getInstance().addObject(ei);
			m.hp -= this.damage;
			Thread.sleep(375);
			
			GameEngine.getInstance().removeObject(ei);
			enemyAttack = false;

			if(old_EnemyHP != m.hp){
				System.out.println("\""+this+ ":  Attacked  =>  " + m+"\"");
				System.out.println(m + ": HP Decreased => " + old_EnemyHP + " -> " + m.hp+"HP\n");
			}

			dead(m);	// Quando morre devido a um ataque
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Coisas a fazer caso um Movable morra
	
	public void dead(Movable m){
		Hero hero = GameEngine.getInstance().getHero();
		if(m.hp <= 0){
			if(!(m instanceof Hero)){
				if(m instanceof Thief)
					for(Items item : m.inventory ){
						item.drop("",m.position,false);
						ImageMatrixGUI.getInstance().addImage((GameElement)item);
					}				
				hero.enemiesKilled++;
			}
			GameEngine.getInstance().removeObject(m);
			System.out.println(m + ": Died 💀");
		}
		hero.finishGame(null,GameEngine.getInstance().getUsername(),0);	// Se os Adversários matarem o Herói o jogo acaba
	}
}
