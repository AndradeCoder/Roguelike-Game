package leo.oop.rogue.game;

import java.util.Iterator;
import java.util.List;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public class Door extends GameElement{

	protected final String nextRoom;
	protected final Point2D roomPosition;
	protected final String keyToOpen;
	protected boolean isOpen;

	public Door(Point2D position, String nextRoom, Point2D roomPosition, String keyToOpen) {
		super(position);
		this.nextRoom=nextRoom;
		this.roomPosition=roomPosition;
		this.keyToOpen=keyToOpen;
	}


	public void changeRoom(){
		Hero hero = GameEngine.getInstance().getHero();
		List<String> openDoors = GameEngine.getInstance().getOpenDoors();
		List<Items> items = hero.inventory;
		Iterator<Items> it = items.iterator();

		if(hero.position.equals(this.position)){
			if(this.keyToOpen.equals("") || openDoors.contains(keyToOpen)){		// Para corredores ou portas abertas
				this.isOpen=true;
				GameEngine.getInstance().changeMap(nextRoom,roomPosition.getX(),roomPosition.getY());
			}

			while(it.hasNext())	{	// Para portas trancadas
				Items item = it.next();
				if(item instanceof Key && ((Key) item).id.equals(keyToOpen)){
					this.isOpen=true;
					it.remove();
					GameEngine.getInstance().removeObject((ImageTile) item);
					openDoors.add(((Key)item).id);
					GameEngine.getInstance().changeMap(nextRoom,roomPosition.getX(),roomPosition.getY());
				}
			}
		}
	}

	@Override
	public String getName() {
		if(keyToOpen.isEmpty())
			switch (GameEngine.getInstance().getCurrentLevel()) {
			case 1: return "DoorWay1";
			case 3: return "DoorWay3";
			}

		if(isOpen)
			return "DoorOpen";
		else 
			return "DoorClosed";
	}

	@Override
	public int getLayer() {
		return 1;
	}

}
