package leo.oop.rogue.game;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Point2D;


public class GameEngine implements Observer {

	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;
	
	// Criar constante FOLDER para otimizar a utilização do FIRSTROOM e de outros quartos
	private static final String FIRSTROOM = "rooms/room0.txt";

	private static GameEngine INSTANCE = null;
	private List<ImageTile> tileList = new ArrayList<>();	// Lista de imagens
	private Map<Integer,ArrayList<ImageTile>> rooms = new HashMap<>();	//Guarda informação dos Rooms
	private List<String> openDoors = new ArrayList<>();		// Portas abertas
	private ImageMatrixGUI gui = ImageMatrixGUI.getInstance();
	private Hero hero = new Hero(null);
	private int turns;
	private int level = 0;
	private String username = "";
	private int score; 

	public static GameEngine getInstance() {
		if (INSTANCE == null)
			INSTANCE = new GameEngine();
		return INSTANCE;
	}

	private GameEngine() {		
		gui.registerObserver(this);
		gui.setSize(GRID_WIDTH, GRID_HEIGHT+1);
		gui.go();
	}

	@Override
	public void update(Observed source) {
		System.out.println("----------  Turn:  "+(turns+1)+ "  -----------------------------");

		int key = ((ImageMatrixGUI) source).keyPressed();
		boolean pressedValidKey = false;
		int states = hero.states.size();

		if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
			pressedValidKey = true;
			hero.move(key);
			turns++;
		}

		if(key == KeyEvent.VK_1 || key == KeyEvent.VK_2 || key == KeyEvent.VK_3) {
			int originalBagSize = hero.inventory.size();
			hero.dropItem(key);
			if(originalBagSize != hero.inventory.size()){	// Se o item foi usado com sucesso
				pressedValidKey = true;
				turns++;
			}
		}

		if(pressedValidKey)		// Movimentação dos adversários
			hero.adversariosMove(turns);

		if(states != hero.states.size())
			System.out.println("States of Hero:  "+hero.states);

		score = turns*5+hero.totalDamageDealt*10+hero.enemiesKilled*3+(int)Math.pow(hero.hp,3.5);
		StatusBar.updatedStatusBar();
		gui.setStatusMessage("POO Game  |  Turns: " +turns+ "  |  Player: " +username+ "  |  HP: " +hero.hp+ "  |  Score: " + score);
		gui.update();
	}


	public void createLevel(String file, int x1, int y1) throws FileNotFoundException{
		File mapFile = new File(file);
		String roomsFolder = FIRSTROOM.substring(0,FIRSTROOM.length()-10);
		String roomFile = file.substring(FIRSTROOM.length()-9,file.length());
		if(!mapFile.exists()){
			if(rooms.size()==0)
				throw new FileNotFoundException("Didn't find starting room file - "+"\"room0.txt\"");

			// Quando este método é invocado em changeMap()
			throw new FileNotFoundException("Room file not found - "+"\""+roomFile+"\" isn't in the \""+roomsFolder+"\" folder");		
		}

		hero.position = new Point2D(x1,y1);
		addObject(hero);

		Scanner sc = new Scanner(mapFile);
		int lines = 0;
		while(sc.hasNextLine()){
			String s = sc.nextLine();
			if(lines < GRID_HEIGHT){					//Para fazer o terreno
				for (int i = 0; i < s.length(); i++) {
					addObject(GameElement.criarObjt(String.valueOf(s.charAt(i)),new Point2D(i,lines),"",null,""));
				}
				lines++;
			} else{								// Para tratar dos elementos do jogo
				String[] s1 = s.split(",");
				if(!s1[0].isEmpty()){
					int x = Integer.parseInt(s1[1]);
					int y = Integer.parseInt(s1[2]);
					Point2D pos = new Point2D(x,y);
					String tipo = s1[0];
					ImageTile i = GameElement.criarObjt(tipo,pos,"",null,"");

					if(s1[0].equals("Key"))
						i = GameElement.criarObjt(tipo,pos,"",null,s1[3]);					

					if(s1[0].equals("Door")){
						String nextRoom = s1[3];
						Point2D roomPosition = new Point2D(Integer.parseInt(s1[4]),Integer.parseInt(s1[5]));

						if(s1.length == 6)
							i = GameElement.criarObjt(tipo,pos,nextRoom,roomPosition,"");
						else{	
							i = GameElement.criarObjt(tipo,pos,nextRoom,roomPosition,s1[6]);
							if(openDoors.contains(s1[6]))
								((Door)i).isOpen = true;
						}
					}
					addObject(i);
				}
			}
		}
		sc.close();
		StatusBar.inicialBar();
		
		// Caso seja um novo mapa atualiza logo a barra de estados, caso contrário seria preciso jogar 1 vez para aparecer atualizada
		StatusBar.updatedStatusBar();
		gui.update();
		rooms.put(level,(ArrayList<ImageTile>) tileList);		// Usado para as animações
	}


	public void start() {
		try {
			createLevel(FIRSTROOM,1,1);
			gui.setStatusMessage("POO Game");

			int canceled = 3;
			while(username.equals("") || username.contains(" ")){
				username = gui.askUser("Insert Username: ");

				while(username == null){	// Tocar no CANCEL, ESC ou fechar a janela
					if(canceled==1){
						gui.setMessage("A Fechar O Jogo....");
						gui.clearImages();
						for (int i = 0; i < GRID_HEIGHT+1; i++) 
							for (int j = 0; j < GRID_WIDTH; j++) 
								gui.addImage(new StatusBar(new Point2D(j,i),"Black"));

						Thread.sleep(1300);
						System.exit(0);
					}
					gui.setMessage("Cancelou\n A Reiniciar O Jogo....");
					username = gui.askUser("==> Se Cancelar Mais " + (canceled-1) + " Vez(es) O Jogo Vai Fechar\n\nInsert Username: ");
					canceled--;
				}

				if(!username.chars().allMatch(chr -> Character.isLetterOrDigit(chr)))
					username = "invalido";

				switch (username) {
				case "invalido" : gui.setMessage("Nome Inválido: ==>  Uso De Caracteres Ilegais\n\n Apenas Pode Usar Caractéres Alfanuméricos"); 
				username = "";
				break;
				case "" : gui.setMessage("Não Inseriu Um Nome!");
				break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void changeMap(String room, int x, int y){
		try {
			ArrayList<ImageTile> aux = new ArrayList<>(tileList);
			Iterator<ImageTile> it = aux.iterator();
			while(it.hasNext()){
				ImageTile i1 = it.next();
				if(hero.inventory.contains(i1))
					it.remove();
			}

			rooms.put(level,aux);
			tileList.removeIf(a -> !a.getName().equals(""));
			gui.clearImages();			
			Thread.sleep(550);

			level = Character.getNumericValue(room.charAt(4));
			if(!rooms.containsKey(level))
				createLevel(FIRSTROOM.substring(0,FIRSTROOM.length()-10)+"/"+room+".txt",x,y);		
			else{
				for(Entry<Integer, ArrayList<ImageTile>> entry : rooms.entrySet())
					if(entry.getKey().equals(Character.getNumericValue(room.charAt(4))))
						for(ImageTile i : entry.getValue()){
							if(i instanceof Door && openDoors.contains(((Door) i).keyToOpen)){
								((Door)i).isOpen=true;	/* Resolve um bug de aparência das portas, quando não é a primeira porta que 
																									foi usada para aceder a um quarto */
							}
							addObject(i);
						}				
				hero.position = new Point2D(x,y);
				gui.update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addObject(ImageTile i) {
		tileList.add(i);
		gui.addImage(i);
	}

	public void removeObject(ImageTile i){
		tileList.remove(i);
		gui.removeImage(i);
	}

	public List<GameElement> getObjects(Point2D p){	// Heroi aparece no index 0 pq é a primeira coisa que é adicionada no ler ficheiro
		List<GameElement> ge = new ArrayList<>();
		for(ImageTile i : tileList)
			if(i instanceof GameElement && i.getPosition().equals(p))
				ge.add((GameElement)i);		
		return ge;
	}

	public Hero getHero(){
		return hero;
	}

	public List<Movable> getAdversarios(){
		List<Movable> list = new ArrayList<>();
		for(ImageTile i : tileList)
			if(i instanceof Movable && !i.equals(hero))
				list.add((Movable)i);
		return list;
	}

	public Map<Integer,ArrayList<ImageTile>> getRooms(){	// Usado para as animações
		return rooms;
	}

	public List<String> getOpenDoors(){
		return openDoors;
	}

	public String getUsername() {
		return username;
	}

	public int getScore(){
		return score;
	}

	public int getCurrentLevel(){	// Para mudar o aspeto dos diferentes niveis nas classes (Floor, Wall e Door)
		return level;
	}
}
