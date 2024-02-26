package leo.oop.rogue.game;

import java.util.List;

import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public class StatusBar extends GameElement{

	private String color;

	public StatusBar(Point2D position, String color) {
		super(position);
		this.color=color;
	}

	public static void inicialBar(){	// Serve para criar a barra de estados default(Vida e Inventário)
		for (int i = 1; i < GameEngine.GRID_WIDTH; i++) {
			StatusBar sb = null;

			if(i < 6)
				sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"Green"+i);
			else sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"Wood_1"); 

			if(sb.position.getX()!=6)
				GameEngine.getInstance().addObject(sb);
		}
		GameEngine.getInstance().addObject(new StatusBar(new Point2D(0,GameEngine.GRID_HEIGHT),"Heart"));	// Simbolo da vida
		GameEngine.getInstance().addObject(new StatusBar(new Point2D(6,GameEngine.GRID_HEIGHT),"Backpack"));	// Simbolo do inventário
	}

	public static void updatedStatusBar(){
		Hero hero = GameEngine.getInstance().getHero();

		// Mostra o inventário
		if(hero.inventory.size() <= 3)
			for (int i = 0; i < hero.inventory.size(); i++) {
				Items item = hero.inventory.get(i);
				// Como está sempre a atualizar as imagens dos items é preciso remover os antigos para não duplicar infinitamente
				GameEngine.getInstance().removeObject((ImageTile) item);	
				GameEngine.getInstance().addObject((ImageTile) hero.pickUpItem(item,i)); 
			}

		// Tudo daqui para baixo é acerca da barra de vida
		String aux = "0";
		StatusBar sb = null;
		int aux1 = 0;

		switch (hero.hp) {
		case 9 : aux = "1"; break;
		case 8 : aux = "1.5"; break;
		case 7 : aux = "2"; break;
		case 6 : aux = "2.5"; break;
		case 5 : aux = "3"; break;
		case 4 : aux = "3.5"; break;
		case 3 : aux = "4"; break;
		case 2 : aux = "4.5"; break;
		case 1 : aux = "5"; break;
		case 0 : aux = "5.5"; break;
		}
		aux1 = Character.getNumericValue(aux.charAt(0));

		// Para não ter objetos StatusBar antigos por debaixo dos novos, de forma a não encher desnecessariamente a tileList e gui
		for (int i = 1; i < 6; i++) {
			List<GameElement> old = GameEngine.getInstance().getObjects(new Point2D(i,GameEngine.GRID_HEIGHT));		
			if(!old.isEmpty())
				for(GameElement g : old)
					GameEngine.getInstance().removeObject(g);
		}

		for (int i = 1; i < 6; i++) 
			if(i > aux1){
				sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"Green"+i);
				GameEngine.getInstance().addObject(sb);
			}

		for (int i = 1; i <= aux1; i++) {
			if(aux.contains("."))
				sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"Red"+i);
			else if(!aux.equals("0")){
				sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"Red"+i);
				if(i==aux1)
					sb = new StatusBar(new Point2D(i,GameEngine.GRID_HEIGHT),"RedGreen"+i);
			}
			GameEngine.getInstance().addObject(sb);
		}
	}


	@Override
	public String getName() {
		switch (color) {
		case "Green1" : return "Green1";
		case "Green2" : return "Green2";
		case "Green3" : return "Green3";
		case "Green4" : return "Green4";
		case "Green5" : return "Green5";
		case "Red1" : return "Red1";
		case "Red2" : return "Red2";
		case "Red3" : return "Red3";
		case "Red4" : return "Red4";
		case "Red5" : return "Red5";
		case "RedGreen1" : return "RedGreen1";
		case "RedGreen2" : return "RedGreen2";
		case "RedGreen3" : return "RedGreen3";
		case "RedGreen4" : return "RedGreen4";
		case "RedGreen5" : return "RedGreen5";
		case "Backpack" : return "Backpack";
		case "Wood_1" : return "Wood_2";
		case "Heart" : return "Heart";
		case "Black" : return "Black";
		}

		return null;
	}

	@Override
	public int getLayer() {
		return 0;
	}

}
