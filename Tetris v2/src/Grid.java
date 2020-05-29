import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Shapes.Block;
import Shapes.Shape;
import Shapes.Block.BlockType;
import Shapes.Shape.ShapeConfig;
import input.Keyboard;

public class Grid {
	public static int width = 10;
	public static int height = 20;
	public static int heightTrue = height + 3;
	public Shape activeShape;
	
	public static int blockGutter = 5; // PIXELS between each block
	public static int blockWidth = 20; 
	public static int blockHeight = blockWidth;
	public static int gridInitOffsetX = 20; // the amount of space the grid should start with for the first block
	public static int gridInitOffsetY = 20;
	
	public Keyboard key;
	Block[][] matrix = new Block[heightTrue][width];
	
	public static int tickDownRate = 2;
	public static int tickRate = 60;
	public static int nanoSecondsPerSec = 1000000000;
	public static double timeBetweenTickDowns = nanoSecondsPerSec / tickDownRate;
	public static long currTime = System.nanoTime();
	public static long timeAtLastTickDown = currTime;
	
	public static boolean rowClearing = false;
	public static Block[][] blockClearer;
	public static int blockClearerIndex = width - 1;
	
	
	public Grid(Keyboard keyArg) {
		key = keyArg;
		for(int y = 0; y < heightTrue; y++) {
			for(int x = 0; x < width; x++) {
				matrix[y][x] = new Block(x, y, ShapeConfig.EMPTY);
			}//for x
		}//for y
	}//constructor
	
	
	
	public void renderBlocks(Graphics g) {
		Block currBlock;
		int yCurse = gridInitOffsetY;
		int xCurse = gridInitOffsetX; 
		
		for(int y = height - 1; y >= 0; y--) {
			for(int x = 0; x < width; x++) {
				currBlock = matrix[y][x];
				
				g.setColor(currBlock.color);
				g.fillRect(xCurse, yCurse, blockWidth, blockHeight);
				xCurse += (blockWidth + blockGutter);
			}
			xCurse = gridInitOffsetX;
			yCurse += (blockHeight + blockGutter);
		}
		
		renderActiveShape(g);
	}//def renderBlocks
	
	
	public void renderActiveShape(Graphics g) {
		Block currBlock;
		int yCurse = gridInitOffsetY;
		int xCurse = gridInitOffsetX;
		
		if(activeShape != null) {
			g.setColor(activeShape.blocks[0].color);
			
			for(int i = 0; i < activeShape.blocks.length; i++) {
				currBlock = activeShape.blocks[i];
				
				xCurse = ((currBlock.x) * (blockWidth + blockGutter)) + gridInitOffsetX;
				yCurse = (((height - 1) - currBlock.y) * (blockHeight + blockGutter)) + gridInitOffsetY;
				
				g.fillRect(xCurse, yCurse, blockWidth, blockHeight);
			}//for 
		}
		
		

	}//def renderActiveShape
	
	
	
	public void moveShapeLeft() {
		//check if there space in the grid to move left
		boolean spaceVacant = true;
		boolean inBounds = true;
		Block currBlock;
		int newX;
		int newY;
		
		for(int i = 0; i < activeShape.blocks.length; i++) {
			currBlock = activeShape.blocks[i];
			newX = currBlock.x - 1;
			newY = currBlock.y;
			
			//check if out of bounds
			if(newX < 0) {
				System.out.println("Move left error: Shape is moving out of bounds");
				inBounds = false;
				break;
			} else {
				//if new space is NOT an empty space
				if(matrix[newY][newX].type != Block.BlockType.EMPTY){
					System.out.println("Move left error: Space occupied by set block");
					spaceVacant = false;
					break;
				}//if 
			}//else
			
		}// for each block
		
		
		if(spaceVacant && inBounds) {
			activeShape.translateLeft();
		}//if vacant and in bounds
		
	}//def moveShapeLeft
	
	public void moveShapeRight() {
		//check if there space in the grid to move left
		boolean spaceVacant = true;
		boolean inBounds = true;
		Block currBlock;
		int newX;
		int newY;
		
		for(int i = 0; i < activeShape.blocks.length; i++) {
			currBlock = activeShape.blocks[i];
			newX = currBlock.x + 1;
			newY = currBlock.y;
			
			//check if out of bounds
			if(newX > width - 1) {
				System.out.println("Move right error: Shape is moving out of bounds");
				inBounds = false;
				break;
			} else {
				//if new space is NOT an empty space
				if(matrix[newY][newX].type != Block.BlockType.EMPTY){
					System.out.println("Move right error: Space occupied by set block");
					spaceVacant = false;
					break;
				}//if 
			}//else
			
		}// for each block
		
		
		if(spaceVacant && inBounds) {
			activeShape.translateRight();
		}//if vacant and in bounds		
	}//def moveShapeRight
	

	public void update() {
		/*
		if(key.keys[KeyEvent.VK_SPACE]) {
			key.gridTest();
		}
		*/
		
		currTime = System.nanoTime();
		if(activeShape == null) {
			activeShape = new Shape(ShapeConfig.SQUARE, Grid.width, Grid.height);
		}
		
		
		key.update();
		
		//LEFT
		if(key.keys[KeyEvent.VK_LEFT]) {
			//INPUT LIMITER
			if(!key.keysTrigger[KeyEvent.VK_LEFT]) {
				key.keysTrigger[KeyEvent.VK_LEFT] = true;
				moveShapeLeft();
			}
		//RIGHT	
		}else if(key.keys[KeyEvent.VK_RIGHT]) {
			//INPUT LIMITER
			if(!key.keysTrigger[KeyEvent.VK_RIGHT]) {
				key.keysTrigger[KeyEvent.VK_RIGHT] = true;
				moveShapeRight();
			}//if keysTrigger
		}// if/else
		else if(key.keys[KeyEvent.VK_DOWN]) {
			activeShape.tickDown();
		}
		
		// ======= check tick down ==============================
		if(currTime - timeAtLastTickDown > timeBetweenTickDowns) {
			timeAtLastTickDown = currTime;
			
			boolean hitGridBottom = false;
			boolean hitSetBlock = false;
			Block currBlock;
			for(int i = 0; i < activeShape.blocks.length; i++) {
				currBlock = activeShape.blocks[i];
				int newY = currBlock.y - 1;
				int newX = currBlock.x;
				if(newY == -1) {
					hitGridBottom = true;
					break;
				}else if(newY < -1) {
					System.out.println("OUT OF BOUNDARY ERROR");
				}
				
				// hit set block
				if(matrix[newY][newX].type != Block.BlockType.EMPTY) {
					hitSetBlock = true;
					break;
				}
				
				
			}//for
			
			//set block if hit grid bottom or out of bounds
			if(hitGridBottom || hitSetBlock) {
				//setting block
				int[] rowChecker = new int[activeShape.blocks.length];
				
				for(int i = 0; i < activeShape.blocks.length; i++) {
					currBlock = activeShape.blocks[i];
					matrix[currBlock.y][currBlock.x] = currBlock;
					rowChecker[i] = currBlock.y;
				}
				activeShape = null;
				
				//check for fully occupied rows
				int rowCompletionCount = 0;
				ArrayList<Integer> toBeClearedRowCords = new ArrayList<Integer>();
				
				for(int i = 0; i < rowChecker.length; i++) {
					int currValue = rowChecker[i];
					boolean isUniqueValue = true;
					for(int k = 0; k < toBeClearedRowCords.size(); k++) {
						if(toBeClearedRowCords.get(k) == currValue) {
							isUniqueValue = false;
							break;
						}
					}
					
					if(isUniqueValue) {
						toBeClearedRowCords.add(currValue);
						rowCompletionCount++;
						
						System.out.println("Y cord set: " + currValue);
					}
					
				}// check each Y cordinate from the set blocks
				System.out.println("Number of Y cords of blocks set: " + toBeClearedRowCords.size());
				
				// !!! FIX ME !!! - DEBUGGING row clear
				
				/*
				
				//check for qualifying fully filled row coordinates
				for(int i = 0; i < toBeClearedRowCords.size(); i++) {
					int currYCord = toBeClearedRowCords.get(i);
					//check for empty blocks in the matrix's row
					boolean rowFullyBlocked = true;
					for(int x = 0; x < matrix[currYCord].length; i++) {
						if(matrix[currYCord][x].type == BlockType.EMPTY) {
							rowFullyBlocked = false;
							break;
						}//if
					}//def for
					
					if(!rowFullyBlocked) {
						toBeClearedRowCords.remove(i);
						i--;
					}
					
				}//for
				
				if(toBeClearedRowCords.size() > 0) {
					rowClearing = true;
					System.out.println("Rows that are fully blocked:");
					Block[][] blockClearer = new Block[toBeClearedRowCords.size()][width];
					int currXCord = width - 1;
					
					for(int i = 0; i < toBeClearedRowCords.size(); i++) {
						int currYCord = toBeClearedRowCords.get(i);
						
						System.out.print("  " + currYCord);
						System.out.println();
						blockClearer[i][currXCord] = matrix[currYCord][currXCord];
					}//for
					
				}//if
				
				*/
				// ====================== !!! FIX ME !!! -- debugging row clear
				
				return;
			}
			
			
			activeShape.tickDown();
		}
		
		
	}//def update
	
}//class
