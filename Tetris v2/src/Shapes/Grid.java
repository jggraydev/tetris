package Shapes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import Shapes.Block.BlockType;
import Shapes.Shape.ShapeConfig;
import input.Keyboard;

public class Grid {
	public int width = 10;
	public int height = 20;
	public int heightTrue = height + 5;
	public Shape activeShape;
	
	public static int blockGutter = 4; // PIXELS between each block
	public static int blockWidth = 30; 
	public static int blockHeight = blockWidth;
	public static int gridInitOffsetX = 30; // the amount of space the grid should start with for the first block
	public static int gridInitOffsetY = 120;
	
	public static int gamePadding = 30;
	public Color textColor = new Color(255, 255, 255);
	
	public Keyboard key;
	public Block[][] matrix = new Block[heightTrue][width];
	
	public int tickDownRate = 2;
	public int tickRate = 60;
	public int nanoSecondsPerSec = 1000000000;
	public double timeBetweenTickDowns = nanoSecondsPerSec / tickDownRate;
	
	public boolean rowClearing = false;
	public Block[][] blockClearer;
	public int blockClearerColIndex = width - 1;
	public int clearRateA = 12;
	public int clearRateB = 3;
	public int clearRateIndex = 0;
	
	public static long score = 0;
	// 1 row = 100
	// 4 rows (aka tetris) = 800
	// subsequent tetrises = 1200
	public boolean isGameOver = false;
	
	public Grid(Keyboard keyArg) {
		key = keyArg;
		for(int y = 0; y < heightTrue; y++) {
			for(int x = 0; x < width; x++) {
				matrix[y][x] = new Block(x, y, ShapeConfig.EMPTY);
			}//for x
		}//for y
	}//constructor
	
	
	public void renderScore(Graphics g, int screenWidth, int screenHeight) {
		Color textColor = new Color(255, 255, 255);
		g.setColor(textColor);
		String textToRender = "Score: " + score;
		int y = gamePadding;
		int x = screenWidth - (gamePadding + 70);
				 
		g.drawString(textToRender, x, y);
	}
	
	public void renderTickDownTimer(Graphics g, int screenWidth, int screenHeight) {
		if(activeShape == null) {
			return;
		}
		
		long timeDelta = activeShape.currTime - activeShape.timeAtLastTickDown;
		double percentagePrecision = timeDelta / timeBetweenTickDowns;
		
		int percentageFillBlock = (int)(percentagePrecision * screenWidth);
		
		
		g.setColor(new Color(150, 0, 0));
		int x = 0;
		int barWidth = percentageFillBlock;
		int barHeight = 3;
		int y = screenHeight - barHeight;
		g.fillRect(x, y, barWidth, barHeight);
	}
	
	public void renderBlocks(Graphics g) {
		Block currBlock;
		int yCurse = gridInitOffsetY;
		int xCurse = gridInitOffsetX; 
		
		for(int y = height - 1; y >= 0; y--) {
			for(int x = 0; x < width; x++) {
				currBlock = matrix[y][x];
				
				g.setColor(currBlock.color);
				g.fillRect(xCurse, yCurse, blockWidth, blockHeight);
				
				// rendering string
				int cx1 = -(blockWidth / 2); //- (blockWidth / 6);
				int cy1 = -(blockHeight / 2); //- (blockHeight / 6);
				
				xCurse -= cx1;
				yCurse -= cy1;
				
				String valString = "" + currBlock.y;
				
				g.setColor(textColor);
				g.drawString(valString, xCurse, yCurse);
				
				xCurse += cx1;
				yCurse += cy1;
				
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
				System.out.println("Move left impeded: Shape is moving out of bounds");
				inBounds = false;
				break;
			} else {
				//if new space is NOT an empty space
				if(matrix[newY][newX].type != Block.BlockType.EMPTY){
					System.out.println("Move left impeded: Space occupied by set block");
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
				System.out.println("Move right impeded: Shape is moving out of bounds");
				inBounds = false;
				break;
			} else {
				//if new space is NOT an empty space
				if(matrix[newY][newX].type != Block.BlockType.EMPTY){
					System.out.println("Move right impeded: Space occupied by set block");
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
		

		if(rowClearing) {
			clearRow();
			return;
		}
		
		if(activeShape == null) {
			activeShape = new Shape(ShapeConfig.SQUARE, this);
			activeShape.justSpawned = true;
			return;
		}
		
		activeShape.currTime = System.nanoTime();
		
		// check if there's no active shape  === > this should be handled by game over , which happens after block SET

		
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
		
		if(activeShape != null) {
			if(activeShape.justSpawned) {
				activeShape.justSpawned = false;
				activeShape.currTime = System.nanoTime();
				activeShape.timeAtLastTickDown = activeShape.currTime;
				return;
			}
			
			if(activeShape.currTime - activeShape.timeAtLastTickDown > timeBetweenTickDowns) {
				activeShape.tickDown();
			}
		}// if activeShape exists
		
		
	}//def update
	
	public void clearRow(){
		if(clearRateIndex < clearRateA) {
			clearRateIndex += clearRateB;
			return;
		}else {
			clearRateIndex = 0;
		}//if/else
		
		int holdX;
		int holdY;
		
		int lastRowIndex = 0;
		
		for(int rowIndex = 0; rowIndex < blockClearer.length; rowIndex++) {
			
			Block currBlock = blockClearer[rowIndex][blockClearerColIndex];
			holdX = currBlock.x;
			holdY = currBlock.y;
			currBlock = new Block(holdX, holdY, ShapeConfig.EMPTY);
			
			matrix[holdY][holdX] = currBlock;
			lastRowIndex = holdY;
		}//for
		
		
		if(blockClearerColIndex == 0) {
			blockClearerColIndex = width - 1;
			rowClearing = false;
					
			incrementScore(blockClearer.length);
			int gapHeight = blockClearer.length;
			shiftRowsDown(lastRowIndex, gapHeight);
			blockClearer = null;

		} else {
			blockClearerColIndex--;
		}//if/else
		
		
	}//def clearRow
	
	
	public void shiftRowsDown(int rowStartIndex, int gapHeight) {
		// !!! FIX ME !!! ===============================
		// when blocks are being moved down, i think a loop iteration variable for a block is holding onto a block after the loop has cleared, causing strange behavior;
		//  could it be that blockClear.length, when first initated, is not reverting back properly?? the block clearer does not change to proper matrix size for containing
		//  the row blocks to be cleared
		//   (partially fixed??) SOLUTION 1: set block clearer back to NULL after it's no longer clearing
		//   SOLUTION 2: move the shifting rows code to its own fucntion
		
		// SHIFTING ROWS DOWN FIX ME
		for(int y = rowStartIndex + 1; y < heightTrue - 1; y++) {
			for(int x = 0; x < width; x++) {
				Block shiftingBlock = matrix[y][x];
				shiftingBlock.y -= gapHeight;
				matrix[y - gapHeight][x] = matrix[y][x];
				
				// !! FIX HERE !! SUSPECTED ERROR -> when the shift occurs, it might be a bad thing to have the upper part set as empty 
				//   the matrix both has the , yet the blocks also have an x,y coordinate that make it somewhat redundant
				//   maybe the matrix might be holding a shape that has not had it's (x, y) values adjusted properly after being shifted.
				//    matrix[1][1] might actually be holding a block that has mismatched (x, y) that are not (1, 1) // --> added shiftingBlock.y -= gapHeight to fix it (not sure if it fixed everything)
				
				matrix[y][x] = new Block(x, y, ShapeConfig.EMPTY);
			}//for x
			//blockClearer = null;
		}//for y
	}
	
	public void incrementScore(int numRows) {
		if(numRows == 1) {
			score += 100;
		}else if(numRows == 2) {
			score += 200;
		}else if(numRows == 3) {
			score += 300;
		}else if(numRows == 4) {
			score += 800;
		}
	}//def incrementScore
	
}//class
