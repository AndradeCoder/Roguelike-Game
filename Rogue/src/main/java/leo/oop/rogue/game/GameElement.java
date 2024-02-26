package leo.oop.rogue.game;


import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;


public abstract class GameElement implements ImageTile{

	protected Point2D position;
	protected boolean isPassable;
	
	public GameElement(Point2D position){
		this.position = position;
	}	

	public Point2D getPosition(){
		return position;
	}

	public static GameElement criarObjt(String tipo, Point2D pos, String nextRoom, Point2D roomPosition, String id){
		switch(tipo){
			//case "Hero" : return new Hero(pos);
			case "Bat" : return new Bat(pos);
			case "Skeleton" : return new Skeleton(pos);
			case "Thug" : return new Thug(pos);
			case "Scorpio" : return new Scorpio(pos);
			case "Thief" : return new Thief(pos);
			case "Key" : return new Key(pos,id);
			case "HealingPotion" : return new HealingPotion(pos);
			case "Sword" : return new Sword(pos);
			case "Door" : return new Door(pos,nextRoom,roomPosition,id);
			case "Treasure" : return new Treasure(pos);
			case "Armor" : return new Armor(pos);
			case "#" : return new Wall(pos);
			
			default :return new Floor(pos);
		}
	}

	@Override
	public String toString() {
		return getName()+position;
	}
}
