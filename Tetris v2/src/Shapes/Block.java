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
	
	public Block() {
		
	}
	
	public Block (int x, int y, ShapeConfig parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		
		if(parent == ShapeConfig.EMPTY) {
			type = BlockType.EMPTY;
			color = new Color(65, 65, 65, 100);
		}else {
			type = BlockType.SET;
			
			
			if(parent == ShapeConfig.LINE) {
				color = new Color(0, 175, 201); // MEDIUM LIGHT BLUE
			} else if(parent == ShapeConfig.SQUARE) {
				color = new Color(222, 209, 0); // YELLOW
			}else if(parent == ShapeConfig.T) {
				color = new Color(175, 0, 212); // PURPLE
			}else if(parent == ShapeConfig.J) {
				color = new Color(217, 146, 0); // ORANGE
			}else if(parent == ShapeConfig.L) {
				color = new Color(0, 122, 202); // BLUE
			}else if(parent == ShapeConfig.S) {
				color = new Color(0, 196, 78); // GREEN
			}else if(parent == ShapeConfig.Z) {
				color = new Color(196, 0, 0); // RED
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
