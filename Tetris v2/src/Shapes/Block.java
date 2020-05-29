package Shapes;
import java.awt.Color;

import Shapes.Shape.ShapeConfig;

public class Block {
	public int x;
	public int y;
	public BlockType type;
	public Color color;
	public ShapeConfig parent;
	
	
	
	public static enum BlockType {
		EMPTY,
		SET
	};
	
	public Block (int x, int y, ShapeConfig parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		
		if(parent == ShapeConfig.EMPTY) {
			type = BlockType.EMPTY;
			color = new Color(0, 0, 0);
		}else {
			type = BlockType.SET;
			
			
			if(parent == ShapeConfig.LINE) {
				color = new Color(0, 191, 255); // MEDIUM LIGHT BLUE
			} else if(parent == ShapeConfig.SQUARE) {
				color = new Color(0, 128, 255); // MEDIUM BLUE
			}else if(parent == ShapeConfig.T) {
				color = new Color(255, 0, 191); // MAGENTA RED 
			}else if(parent == ShapeConfig.J) {
				color = new Color(157, 141, 0); // OCHRE
			}else if(parent == ShapeConfig.L) {
				color = new Color(216, 191, 3); // gold
			}else if(parent == ShapeConfig.S) {
				color = new Color(206, 125, 3); // med orange
			}else if(parent == ShapeConfig.Z) {
				color = new Color(252, 54, 166); // hot pink
			}else if(parent == ShapeConfig.TEST) {
				color = new Color(255, 255, 255); // white
			}else {
				System.out.println("PARENT SHAPE ERROR");
				color = new Color(255, 0, 0); // RED
			}
			
			
			
			
			
		}
	}
	
	
	public void translateLeft() {
		x--;
	}
	
	public void translateRight() {
		x++;
	}
	
	public void tickDown() {
		y--;
	}
	
	
}
