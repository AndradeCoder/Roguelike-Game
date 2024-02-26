package leo.oop.rogue.game;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.gui.ImageMatrixGUI;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class Thief extends Movable{

	public Thief(Point2D position) {
		super(position);
		hp=5;
	}

	@Override
	public void attack(Movable m) {
		List<Items> heroItems = GameEngine.getInstance().getHero().inventory;
		int itemToSteal = (int) (Math.random()*4);
		if(!heroItems.isEmpty()){
			while(heroItems.size()<=itemToSteal || heroItems.get(itemToSteal) == null)
				itemToSteal = (int) (Math.random()*4);

			this.inventory.add(heroItems.get(itemToSteal));
			ImageMatrixGUI.getInstance().removeImage((ImageTile) heroItems.get(itemToSteal));
			heroItems.get(itemToSteal).drop("Drop",null,true);
			heroItems.remove(itemToSteal);
		}
	}

	@Override
	public void move() {
		if(this.inventory.isEmpty())
			super.move();
		else{
			Hero hero = GameEngine.getInstance().getHero();
			int beforeStealing = this.inventory.size();
			this.dir = hero.dir.opposite();
			Vector2D vec = dir.asVector();
			boolean hasMoved = false;
			List<Direction> listDir = new ArrayList<>();
			List<GameElement> ge = GameEngine.getInstance().getObjects(this.position.plus(vec));
			int aux = 0;

			while(!hasMoved){

				// Vai verificar se pode andar, caso não possa e esteja lá o heroi, vai tentar roubar
				if((canMove(this.position.plus(vec)) && !this.dir.equals(hero.dir)) || (ge.size()>=2 && ge.get(1) instanceof Items)){
					this.position = this.position.plus(vec);
					hasMoved = true;
				}
				else if(beforeStealing != this.inventory.size()){	// Se roubou acaba a sua jogada
					return;
				}
				// Se não andou ou roubou significa que vamos procurar por uma direção por onde se possa andar
				else {
					if(aux==0){
						for(Direction dr : Direction.values())
							if(!dr.equals(hero.dir) && !dr.equals(this.dir))
								listDir.add(dr);	// Direções do eixo oposto

						this.dir = listDir.get((int)(Math.random()*listDir.size()));	//Das duas direções escolhe aleatóriamente uma delas
						vec = this.dir.asVector();
						aux++;
					}
					else{	
						listDir.remove(this.dir);	// Se a direção que foi escolhida não é válida vai para a outra
						
						// Em ultimo caso ou anda na mesma direção do heroí ou fica parado e acaba a sua jogada
						if(listDir.isEmpty()){
							vec = hero.dir.asVector();
							if(canMove(this.position.plus(vec))){
								this.position = this.position.plus(vec);
								return;
							}
							return;
						}

						this.dir = listDir.get(0);
						vec = this.dir.asVector();
					}
				}				
			}
		}
		dead(this);		// Quando morre sem ter sido diretamente atacado
	}

	@Override
	public String getName() {
		return "Thief";
	}

	@Override
	public int getLayer() {
		return 2;
	}

}
