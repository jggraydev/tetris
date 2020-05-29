package Shapes;

import Shapes.Shape.ShapeConfig;

public class Shape {
	public Block[] blocks; // = new Block[];
	public ShapeConfig config;
	
	public static enum ShapeConfig {
		LINE,
		SQUARE,
		T,
		J,
		L,
		S,
		Z,
		TEST,
		EMPTY
		
	}
	
	public Shape(ShapeConfig config, int gridWidth, int gridHeight) {
		this.config = config;
		int gridWidthMiddle = gridWidth / 2;
		
		if(config == ShapeConfig.TEST) {
			blocks = new Block[1];
			blocks[0] = new Block(5, 0, ShapeConfig.TEST);
			
			
		}else if(config == ShapeConfig.EMPTY) {
			
		}else if(config == ShapeConfig.SQUARE) {
			
			ShapeConfig parent = ShapeConfig.SQUARE;
			
			blocks = new Block[4];
			blocks[0] = new Block(gridWidthMiddle - 1, gridHeight, parent);
			blocks[1] = new Block(gridWidthMiddle, gridHeight, parent);
			blocks[2] = new Block(gridWidthMiddle - 1, gridHeight + 1, parent);
			blocks[3] = new Block(gridWidthMiddle, gridHeight + 1, parent);
			
			
		}
	}
	
	public void translateLeft() {
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.translateLeft();
		}
	}
	
	public void translateRight() {
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.translateRight();
		}
	}
	
	public void tickDown() {
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.tickDown();
		}
	}
	
	public Shape getNewShape(int gridWidth, int gridHeight) {
		return new Shape(ShapeConfig.SQUARE, gridWidth, gridHeight);
	}
	
	
}
