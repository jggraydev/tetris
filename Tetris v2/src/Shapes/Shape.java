package Shapes;

import java.util.ArrayList;

import Shapes.Block.BlockType;
import Shapes.Shape.ShapeConfig;

public class Shape {
	public Block[] blocks; // = new Block[];
	public ShapeConfig config;
	public Grid grid;
	
	public long currTime;
	public long timeAtLastTickDown;
	public boolean justSpawned;
	
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
	
	public Shape(ShapeConfig config, Grid grid) {
		this.config = config;
		this.grid = grid;
		
		int gridWidth = grid.width;
		int gridHeight = grid.height;
		
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
			
			
		}//else
	}// constructor
	
	
	public Shape getNewShape(Grid grid) {
		Shape newShape = new Shape(ShapeConfig.SQUARE, grid);
		return newShape;
	}//def getNewShape
	
	
	public void translateLeft() {
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.translateLeft();
		}
	}//def translateLeft
	
	
	public void translateRight() {
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.translateRight();
		}
	}//def translateRight
	
	
	public void tickDown() {
		timeAtLastTickDown = System.nanoTime();
		//Set up booleans and see if block ticking down will hit something
		boolean hitGridBottom = false;
		boolean hitSetBlock = false;
		Block currBlock;
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			int newY = currBlock.y - 1;
			int newX = currBlock.x;
			if(newY == -1) {
				hitGridBottom = true;
				break;
			}else if(newY < -1) {
				System.out.println("OUT OF BOUNDARY ERROR");
			}
			
			// hit set block
			if(grid.matrix[newY][newX].type != Block.BlockType.EMPTY) {
				hitSetBlock = true;
				break;
			}//if
		}//for
		
		
		//set block if hit grid bottom or out of bounds
		if(hitGridBottom || hitSetBlock) {
		
			setBlock();
			return;
		}
	
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			currBlock.tickDown();
		}
	}//def tickDown
	
	
	
	public void setBlock() {
		int[] rowChecker = new int[blocks.length];
		Block currBlock;
		
		// puts the y cordinate of each set block into array rowChecker
		for(int i = 0; i < blocks.length; i++) {
			currBlock = blocks[i];
			grid.matrix[currBlock.y][currBlock.x] = currBlock;
			rowChecker[i] = currBlock.y;
		}
		grid.activeShape = null;
		
		//check for fully occupied rows
		int affectedRowsCount = 0;
		ArrayList<Integer> toBeClearedRowCords = new ArrayList<Integer>();
		
		for(int i = 0; i < rowChecker.length; i++) {
			int currValue = rowChecker[i];
			boolean isUniqueValue = true;
			for(int k = 0; k < toBeClearedRowCords.size(); k++) {
				if(toBeClearedRowCords.get(k) == currValue) {
					isUniqueValue = false;
					break;
				}//if
			}//for
			
			// only add row index if it's unique
			if(isUniqueValue) {
				toBeClearedRowCords.add(currValue);
				affectedRowsCount++;	
				System.out.println("Y cord set: " + currValue);
			}//if
			
		}// check each Y cordinate from the set blocks
		System.out.println("Number of Y cords of blocks set: " + toBeClearedRowCords.size());
		
		
		
		
		//check for qualifying fully filled row coordinates
		for(int i = 0; i < toBeClearedRowCords.size(); i++) {
			int currYCord = toBeClearedRowCords.get(i);
			System.out.println(" currYCord = " + currYCord);
			
			//check for empty blocks in the matrix's row
			boolean rowFullyBlocked = true;
			
			
			for(int x = 0; x < grid.width; x++) {
				if(grid.matrix[currYCord][x].type == BlockType.EMPTY) {
					rowFullyBlocked = false;
					break;
				}//if
			}//def for
			
			if(!rowFullyBlocked) {
				int currRowIndex = toBeClearedRowCords.get(i);
				System.out.println("  Row not fully blocked: " + currRowIndex);
				toBeClearedRowCords.remove(i);
				i--;
			}
		}//for
		
		
		if(toBeClearedRowCords.size() > 0) {
			grid.rowClearing = true;
			
			System.out.print("   >>> Rows that are fully blocked:");
			
			for(int i = 0; i < toBeClearedRowCords.size(); i++) {
				int rowIndex = toBeClearedRowCords.get(i);
				System.out.print(" " + rowIndex);
			}
			System.out.println();
			
			
			int numOfRowsClearing = toBeClearedRowCords.size();
			grid.blockClearer = new Block[numOfRowsClearing][grid.width];
			
			for(int i = 0; i < numOfRowsClearing; i++) {
				int rowIndex = toBeClearedRowCords.get(i);
				
				for(int k = 0; k < grid.width; k++) {
					grid.blockClearer[i][k] = grid.matrix[rowIndex][k];
				}//for column
			}//for rows	
		}//if
		
		
		//set block needs to check for a game over
		//if any blocks exist above the red line
		
		checkGameOver();
		
	}//def setBlock
	
	
	public void checkGameOver() {
		
	}//def checkGameOver	
	
}//class