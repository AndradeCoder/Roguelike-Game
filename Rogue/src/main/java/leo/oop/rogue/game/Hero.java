package leo.oop.rogue.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;


public class Hero extends Movable{

	protected int enemiesKilled;
	protected int totalDamageDealt;

	public Hero(Point2D position) {
		super(position);
		hp=10;
		damage=1;
	}	

	public void move(int key) {
		if(states.contains(States.POISENED))
			this.hp--;

		dir = Direction.directionFor(key);
		Vector2D vector = dir.asVector();
		Point2D beforeMoving = position;
		Point2D newPos = position.plus(vector);
		boolean hasMoved = false;
		List<GameElement> ge = GameEngine.getInstance().getObjects(newPos);
		Movable m = null;

		// Não dá erro se tentar sair de um quarto logo após ter entrado, ou seja, se ainda estiver dentro da porta

		if(ge.size()>=2 && ge.get(1) instanceof Movable)	// Interação com adversários
			m = (Movable) ge.get(1);
		else if(ge.size()>=2 && !(ge.get(1) instanceof Movable)){	// Interação com items e portas
			position = newPos;
			
			// Se o tesouro está na próxima posição o jogo acaba
			finishGame(ge.get(1),GameEngine.getInstance().getUsername(),GameEngine.getInstance().getScore());

			if(ge.get(1) instanceof Items && this.inventory.size() <3){
				((Items)ge.get(1)).use("Use");	// Items que dão buffs ao jogador
				this.inventory.add((Items)ge.get(1));
				System.out.println(this + ": Picked up Item => " + (Items)ge.get(1));
			}

			if(ge.get(1) instanceof Door){
				((Door) ge.get(1)).changeRoom();
				
				if(!((Door) ge.get(1)).isOpen)				// Se a porta estiver trancada e não tiver a chave não mexe
					this.position = beforeMoving; 
			}
			hasMoved = true;
		}
		
		if(canMove(newPos) && !hasMoved)
			position = newPos;
		else if(!canMove(newPos) && !hasMoved && ge.size() >=2){
			attack(m);
		}

		dead(this);		// Quando morre sem ter sido diretamente atacado (por exemplo: veneno)
	}


	private Items removeFromInventory(Items ge){	// Larga o item numa posição aleatória à volta do Herói
		List<Point2D> availablePositionsForItem = this.position.getNeighbourhoodPoints();
		List<Point2D> validPositionsForItem = new ArrayList<>();
		Point2D pos = null;

		for(Items ge2 : this.inventory)
			if(((GameElement) ge2).getName().equals(((GameElement) ge).getName())){
				ge2 = ge;
				for(Point2D p : availablePositionsForItem)
					if(canMove(p))
						validPositionsForItem.add(p);

				// Se Não existe nenhum sítio onde se possa largar, incluindo a posição atual, não faz nada e envia uma msg de aviso
				List<GameElement> list = GameEngine.getInstance().getObjects(this.position);
				if(list.size()>2 && validPositionsForItem.isEmpty()){
					ImageMatrixGUI.getInstance().setMessage("ERRO: Não Existe Um Sítio Disponível Para Largar Items");
					return null;
				}

				ge2.drop("Drop",null,false);	// Uso de consumiveis ou remoção de buffs

				if(!validPositionsForItem.isEmpty()){
					pos = validPositionsForItem.get((int) (Math.random()*validPositionsForItem.size()));
					ge2.drop("",pos,false);
					return ge2;
				} else {							// Caso não possa largar numa posição à volta, larga na posição atual do Herói
					ge2.drop("",this.position,false);
					return ge2;
				}
			}					
		return null;
	}

	// Escolha do item a remover do inventário
	public void dropItem(int key){ 
		ImageMatrixGUI gui = ImageMatrixGUI.getInstance();
		int slotNumber = 0;

		switch (key) {
		case KeyEvent.VK_1 : slotNumber = 1;
		break;
		case KeyEvent.VK_2 : slotNumber = 2;
		break;
		case KeyEvent.VK_3 : slotNumber = 3;
		break;
		}

		if(this.inventory.isEmpty())
			gui.setMessage("O SEU INVENTÁRIO ESTÁ VAZIO!");

		String s = "";
		while(this.inventory.size() >= 1 && slotNumber > this.inventory.size() && slotNumber >= 2){
			if(slotNumber > 3)
				s = "ERRO:  NÃO EXISTE O SLOT:  ( ";
			else s = "ERRO:  NÃO TEM ITEMS EQUIPADOS NO SLOT:  ( ";

			String aux = "";
			for (int i = 1; i <= inventory.size(); i++){
				String aux1 = ", ";
				if( i < inventory.size())
					aux += Integer.toString(i) + aux1;
				else aux += Integer.toString(i);
			}

			String slot = gui.askUser(s+slotNumber+" )\n\n Escolha Entre O(s) Slot(s):  ( "+ aux +" )\n" + "Caso Contrário Pressione CANCEL ou ESC");
			if(slot == null)	// Tocar no CANCEL, ESC ou fechar a janela
				return;

			boolean b = false;
			for (int i = 0; i < slot.length() || i==0; i++) 
				if(slot.isEmpty() || !Character.isDigit(slot.charAt(i))){
					gui.setMessage("ERRO:  Não Inseriu Corretamente Um Número!");
					slotNumber=4;	// Podia ser qualquer valor, desde que fosse maior que o tamanho limite do inventário (3)
					b=true;
					break;
				}

			if(!b)
				slotNumber = Integer.parseInt(slot);
		}

		if(!this.inventory.isEmpty()){
			Items item = this.inventory.get(slotNumber-1);
			if (removeFromInventory(item) != null){
				System.out.println(this + ": Droped Item => " + item);
			}

			if(states.contains(States.POISENED))
				this.hp--;
		}
	}

	public Items pickUpItem(Items ge, int x){	// Mete a posição dos items que estão no inventário para a zona do inventário na tela
		for(Items i : this.inventory)
			if(((GameElement) i).getName().equals(((GameElement) ge).getName())){
				i = ge;
				((GameElement)i).position = new Point2D(x+7,GameEngine.GRID_HEIGHT);
				return i;
			}			
		return null;
	}

	public void adversariosMove(int a){		// Movimentação dos adversários
		Iterator<Movable> it = GameEngine.getInstance().getAdversarios().iterator();
		while(it.hasNext()){
			Movable m = it.next();
			if(m instanceof Skeleton){
				if(a%2 ==0)
					m.move();
			} 
			else m.move();
		}
	}

	public void finishGame(GameElement ge, String username, int score){
		String ending = "";
		ImageMatrixGUI gui = ImageMatrixGUI.getInstance();

		if(ge instanceof Treasure && this.position.equals(ge.position)){
			ending = "    " + "Encontraste o tesouro" + "\n                Ganhaste!\n    🏆🏆🏆🏆🏆🏆🏆🏆🏆🏆";
			gui.setStatusMessage("POO GAME  |  VICTORY! 🏆 |   Your Score: " + score);
		}
		if(this.hp <= 0){
			ending = "        " + "FICASTE SEM VIDA" + "\n              GAME OVER!\n   💀💀💀💀💀💀💀💀💀💀";
			gui.setStatusMessage("POO GAME  |  YOU ARE DEAD 💀  |   Your Score: " + score);
			StatusBar.updatedStatusBar();
		}
		if(!ending.equals("")){
			try {
				gui.setMessage(ending);
				gui.clearImages();
				for (int i = 0; i < GameEngine.GRID_HEIGHT+1; i++) 
					for (int j = 0; j < GameEngine.GRID_WIDTH; j++) 
						gui.addImage(new StatusBar(new Point2D(j,i),"Black"));
				
				Thread.sleep(1300);
				scoreboard(username,score);
				System.exit(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void scoreboard(String username, int score){
		Map<String,Integer> scores = new HashMap<>();
		scores.put(username,score);
		File f = new File("Leaderboard.txt");

		try {
			Scanner sc = new Scanner(f);
			int lines = 0;
			while(sc.hasNextLine()){
				String str = sc.nextLine();
				if(lines >=2 && !str.isEmpty()){
					String[] s = str.split("  | ");
					scores.put(s[1],Integer.parseInt(s[3]));
				}
				lines++;
			}
			sc.close();

			List<Map.Entry<String,Integer>> listOfEntries = new ArrayList<>(scores.entrySet());		 
			listOfEntries.sort((e1,e2) -> e2.getValue()-e1.getValue());
			if(listOfEntries.size()==6)
				listOfEntries.remove(5);

			Map<String,Integer> sortedScores = new LinkedHashMap<>();		// Mapa dos melhores scores, até 5, organizados de forma crescente
			for(Map.Entry<String,Integer> entry : listOfEntries)		
				sortedScores.put(entry.getKey(),entry.getValue());

			PrintWriter pw = new PrintWriter(f);
			int i = 1;		
			if(f.length()==0)
				pw.println("Top 5 players: \n");		
			for (Map.Entry<String,Integer> entry : sortedScores.entrySet()){
				pw.println(i + ". " + entry.getKey() + "  ==>  " + entry.getValue());
				i++;
			} 
			pw.close();

		} catch (FileNotFoundException e) {
			System.out.println("Leaderboard file not found: Your game was not registered");
			ImageMatrixGUI.getInstance().setMessage("ERROR: No Leaderboard file found");
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		if(hp>8 && states.contains(States.POISENED))
			return"HeroPoisoned";
		if(hp<=8 && hp>5){
			if(states.contains(States.POISENED))
				return "HeroDamaged&Poisened_1";
			return "HeroDamaged_1";
		}
		if(hp<=5 && hp>3){
			if(states.contains(States.POISENED))
				return "HeroDamaged&Poisened_2";
			return "HeroDamaged_2";
		}
		if(hp<=3){
			if(states.contains(States.POISENED))
				return "HeroDamaged&Poisened_3";
			return "HeroDamaged_3";
		}
		return "Hero";
	}

	@Override
	public int getLayer() {
		return 3;
	}

}
