package Shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import Shapes.Block.BlockType;
import soundfx.Sound;
import soundfx.SoundEffect;

public class Shape {
	public Block[] blocks; // = new Block[];
	public ShapeConfig config;
	public Grid grid;
	
	public long currTime;
	public long timeAtLastTickDown;
	public boolean justSpawned;
	
	public int ShapeConfigCount = 7;
	public int rotationCycle = 0;
	
	public int orientations = 1;
	public int maxRotationIndex;
	
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
		int gridHeight = grid.height + grid.heightLeeway;
		
		int gridWidthMiddle = gridWidth / 2;
		
		if(config == ShapeConfig.TEST) {
			blocks = new Block[1];
			blocks[0] = new Block(5, 0, ShapeConfig.TEST);
		}else if(config == ShapeConfig.EMPTY) {
			// nothing
		}else {
			ShapeConfig parent = config;
			blocks = new Block[4];
			
			if(config == ShapeConfig.SQUARE) {
				orientations = 1;
				
				blocks[0] = new Block(gridWidthMiddle - 1, gridHeight, parent);
				blocks[1] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[2] = new Block(gridWidthMiddle - 1, gridHeight + 1, parent);
				blocks[3] = new Block(gridWidthMiddle, gridHeight + 1, parent);
			}else if(config == ShapeConfig.LINE) {	
				orientations = 2;
				blocks[0] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[1] = new Block(gridWidthMiddle + 1, gridHeight, parent);
				blocks[2] = new Block(gridWidthMiddle - 1, gridHeight, parent);
				blocks[3] = new Block(gridWidthMiddle -2, gridHeight, parent);	
			}else if(config == ShapeConfig.T) {
				orientations = 4;
				blocks[0] = new Block(gridWidthMiddle, gridHeight + 1, parent);
				blocks[1] = new Block(gridWidthMiddle - 1, gridHeight + 1, parent);
				blocks[2] = new Block(gridWidthMiddle + 1, gridHeight + 1, parent);
				blocks[3] = new Block(gridWidthMiddle, gridHeight, parent);	
			}else if(config == ShapeConfig.J) {
				orientations = 4;
				blocks[0] = new Block(gridWidthMiddle, gridHeight + 1, parent);
				blocks[1] = new Block(gridWidthMiddle, gridHeight + 2, parent);	
				blocks[2] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[3] = new Block(gridWidthMiddle - 1, gridHeight, parent);
			}else if(config == ShapeConfig.L) {
				orientations = 4;
				gridWidthMiddle--; // ROOT block[0] is weighted left
				blocks[0] = new Block(gridWidthMiddle, gridHeight + 1, parent);
				blocks[1] = new Block(gridWidthMiddle, gridHeight + 2, parent);	
				blocks[2] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[3] = new Block(gridWidthMiddle + 1, gridHeight, parent);
			}else if(config == ShapeConfig.S) {
				orientations = 2;
				blocks[0] = new Block(gridWidthMiddle, gridHeight + 1, parent);
				blocks[1] = new Block(gridWidthMiddle + 1, gridHeight + 1, parent);
				blocks[2] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[3] = new Block(gridWidthMiddle - 1, gridHeight, parent);
				
				
			}
			else if(config == ShapeConfig.Z) {
				orientations = 2;
				blocks[0] = new Block(gridWidthMiddle, gridHeight + 1, parent);
				blocks[1] = new Block(gridWidthMiddle - 1, gridHeight + 1, parent);
				blocks[2] = new Block(gridWidthMiddle, gridHeight, parent);
				blocks[3] = new Block(gridWidthMiddle + 1, gridHeight, parent);
				
				
			}
		}
		maxRotationIndex = orientations - 1;
	}// constructor
	
	
	
	
	// gets random next shape
	public static Shape getNewShape(Grid grid) {
		Random rand = new Random();
		
		int nextShapeInt = rand.nextInt(7);
		nextShapeInt = rand.nextInt(7);
		
		
		Shape newShape;
		
		if(nextShapeInt == 0) {
			newShape = new Shape(ShapeConfig.LINE, grid);
		}else if(nextShapeInt == 1) {
			newShape = new Shape(ShapeConfig.SQUARE, grid);
		}else if(nextShapeInt == 2) {
			newShape = new Shape(ShapeConfig.T, grid);
		}else if(nextShapeInt == 3) {
			newShape = new Shape(ShapeConfig.J, grid);
		}else if(nextShapeInt == 4) {
			newShape = new Shape(ShapeConfig.L, grid);
		}else if(nextShapeInt == 5) {
			newShape = new Shape(ShapeConfig.S, grid);
		}else if(nextShapeInt == 6) {
			newShape = new Shape(ShapeConfig.Z, grid);
		}else {
			System.out.println("Next shape random error >> TEST SHAPE");
			newShape = new Shape(ShapeConfig.TEST, grid);
		}
		
		return newShape;
	}//def getNewShape
	
	
	
	
	
	
	
	
	// =======================================================================
	// RECONFIGURE SHAPE =====================================================
	// =======================================================================
	public void reconfigureShape(int oldCycle) {
		if(orientations == 1) {
			System.out.println("no shape rotation");
			return;
		}
		
		Block[] coordHolder = new Block[blocks.length];
		for(int i = 0; i < coordHolder.length; i++) {
			coordHolder[i] = new Block();
		}
		coordHolder[0].x = blocks[0].x;
		coordHolder[0].y = blocks[0].y;
		
		int rootX = coordHolder[0].x;
		int rootY = coordHolder[0].y;
		
		if(rotationCycle > maxRotationIndex) {
			rotationCycle = 0;
		}else if(rotationCycle < 0) {
			rotationCycle = maxRotationIndex;
		}
		
		if(config == ShapeConfig.LINE) {
			if(rotationCycle == 1) {
				//       0   1   2   3
				//	3	[ ] [ ] [3] [ ]
				//	2	[ ] [ ] [2] [ ]
				//	1	[ ] [ ] [0] [ ]
				//	0	[ ] [ ] [1] [ ]
				System.out.println("LN rotation cycle: " + rotationCycle);
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY -1);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY + 1); 
										 
				coordHolder[3].x = (rootX);	 
				coordHolder[3].y = (rootY + 2);

				
			} else if(rotationCycle == 0) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [ ] [ ]
				//	1	[3] [2] [0] [1]
				//	0	[ ] [ ] [ ] [ ]
				
				coordHolder[1].x = (rootX + 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX - 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX - 2);	 
				coordHolder[3].y = (rootY);
			}
		}// LINE
		
		else if(config == ShapeConfig.SQUARE) {
			// orientations = 1 ===> DO NOTHING
		}//SQUARE
		
		else if(config == ShapeConfig.T) {
			// orientations = 4
						
			if(rotationCycle == 0) {
				
				coordHolder[1].x = (rootX - 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX + 1);		//		 0   1   2   3
				coordHolder[2].y = (rootY);    // 3	[ ] [ ] [ ] [ ]
										 // 2	[ ] [ ] [ ] [ ]
				coordHolder[3].x = (rootX);	 // 1	[ ] [1] [0] [2]
				coordHolder[3].y = (rootY - 1); // 0	[ ] [ ] [3] [ ]
			} else if(rotationCycle == 1) {
				
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY + 1);  // 3	[ ] [ ] [ ] [ ]
									 		// 2	[ ] [ ] [1] [ ]
				coordHolder[2].x = (rootX); 		// 1	[ ] [3] [0] [ ]
				coordHolder[2].y = (rootY - 1); 	// 0	[ ] [ ] [2] [ ]
				
				coordHolder[3].x = (rootX - 1);
				coordHolder[3].y = (rootY);
				
			} else if(rotationCycle == 2) {

											//       0   1   2   3
				 							//	3	[ ] [ ] [ ] [ ]
				 							//	2	[ ] [ ] [3] [ ]
											//	1	[ ] [2] [0] [1]
				coordHolder[1].x = (rootX + 1); //	0	[ ] [ ] [ ] [ ]
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX - 1);
				coordHolder[2].y = (rootY);
				
				coordHolder[3].x = (rootX);
				coordHolder[3].y = (rootY + 1);
				
			}else if(rotationCycle == 3) {				
												//       0   1   2   3
												//	3	[ ] [ ] [ ] [ ]
												//	2	[ ] [ ] [2] [ ]
												//	1	[ ] [ ] [0] [3]
				coordHolder[1].x = (rootX); 	//	0	[ ] [ ] [1] [ ]
				coordHolder[1].y = (rootY - 1);
				
				coordHolder[2].x = (rootX);
				coordHolder[2].y = (rootY + 1);
				
				coordHolder[3].x = (rootX + 1);
				coordHolder[3].y = (rootY);
			}
			
		}//T
		else if(config == ShapeConfig.J) {			
			if(rotationCycle == 0) {
				
			//	   0   1   2   3
	        // 3  [ ] [ ] [ ] [ ]
            // 2  [ ] [ ] [1] [ ]
		    // 1  [ ] [ ] [0] [ ]
	        // 0  [ ] [3] [2] [ ]
				
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY + 1);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY - 1); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY - 1);
				
			} else if(rotationCycle == 1) {
				//		 0   1   2   3
			 	// 3	[ ] [ ] [ ] [ ]
		  		// 2	[ ] [3] [ ] [ ]
				// 1	[ ] [2] [0] [1]
				// 0	[ ] [ ] [ ] [ ]
				coordHolder[1].x = (rootX + 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX - 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY + 1);				
			} else if(rotationCycle == 2) {

				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [2] [3]
				//	1	[ ] [ ] [0] [ ]
				//	0	[ ] [ ] [1] [ ]
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY - 1);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY + 1); 
										 
				coordHolder[3].x = (rootX + 1);	 
				coordHolder[3].y = (rootY + 1);		
				
			}else if(rotationCycle == 3) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [ ] [ ]
				//	1	[ ] [1] [0] [2]
				//	0	[ ] [ ] [ ] [3]
				coordHolder[1].x = (rootX - 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX + 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX + 1);	 
				coordHolder[3].y = (rootY - 1);				
			}
	
		}//J
		
		else if(config == ShapeConfig.L) {			
			if(rotationCycle == 0) {
				
			//	   0   1   2   3
	        // 3  [ ] [ ] [ ] [ ]
            // 2  [ ] [1] [ ] [ ]
		    // 1  [ ] [0] [ ] [ ]
	        // 0  [ ] [2] [3] [ ]
				
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY + 1);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY - 1); 
										 
				coordHolder[3].x = (rootX + 1);	 
				coordHolder[3].y = (rootY - 1);
				
			} else if(rotationCycle == 1) {
				//		 0   1   2   3
			 	// 3	[ ] [ ] [ ] [ ]
		  		// 2	[ ] [ ] [ ] [ ]
				// 1	[2] [0] [1] [ ]
				// 0	[3] [ ] [ ] [ ]
				coordHolder[1].x = (rootX + 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX - 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY - 1);				
			} else if(rotationCycle == 2) {

				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[3] [2] [ ] [ ]
				//	1	[ ] [0] [ ] [ ]
				//	0	[ ] [1] [ ] [ ]
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY - 1);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY + 1); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY + 1);		
				
			}else if(rotationCycle == 3) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [3] [ ]
				//	1	[1] [0] [2] [ ]
				//	0	[ ] [ ] [ ] [ ]
				coordHolder[1].x = (rootX - 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX + 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX + 1);	 
				coordHolder[3].y = (rootY + 1);		
			
			}
			
		}//L
		
		if(config == ShapeConfig.S) {
			if(rotationCycle == 0) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [ ] [ ]
				//	1	[ ] [ ] [0] [1]
				//	0	[ ] [3] [2] [ ]
				coordHolder[1].x = (rootX + 1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY - 1); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY - 1);

				
			} else if(rotationCycle == 1) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [3] [ ] [ ]
				//	1	[ ] [2] [0] [ ]
				//	0	[ ] [ ] [1] [ ]
				
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY-1);
				
				coordHolder[2].x = (rootX - 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY + 1);
			}
		}// S
		
		if(config == ShapeConfig.Z) {
			if(rotationCycle == 0) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [ ] [ ]
				//	1	[ ] [1] [0] [ ]
				//	0	[ ] [ ] [2] [3]
				coordHolder[1].x = (rootX -1);
				coordHolder[1].y = (rootY);
				
				coordHolder[2].x = (rootX);	
				coordHolder[2].y = (rootY - 1); 
										 
				coordHolder[3].x = (rootX + 1);	 
				coordHolder[3].y = (rootY - 1);

				
			} else if(rotationCycle == 1) {
				//       0   1   2   3
				//	3	[ ] [ ] [ ] [ ]
				//	2	[ ] [ ] [1] [ ]
				//	1	[ ] [2] [0] [ ]
				//	0	[ ] [3] [ ] [ ]
				
				coordHolder[1].x = (rootX);
				coordHolder[1].y = (rootY + 1);
				
				coordHolder[2].x = (rootX - 1);	
				coordHolder[2].y = (rootY); 
										 
				coordHolder[3].x = (rootX - 1);	 
				coordHolder[3].y = (rootY - 1);
			}
		}// S
		

		
		// code for all shapes
		for(int i = 0; i < coordHolder.length;i++) {
			int x = coordHolder[i].x;
			int y = coordHolder[i].y;
			int xBound = grid.width - 1;
			int yBound = grid.heightTrue - 1;
			
			//if new position of block is obstructed, reset rotationCycle and do nothing
			if( (x > xBound) || (x < 0) || (y < 0) || (y > yBound) || grid.matrix[y][x].type == BlockType.SET  ) {
				System.out.println("Rotation nullfied: out of bounds OR set block obstruction");
				rotationCycle = oldCycle;
				return;
			}

			
		}//for
		
		
		//if 
		for(int i = 0; i < coordHolder.length; i++) {
			int x = coordHolder[i].x;
			int y = coordHolder[i].y;
			
			blocks[i].x = x;
			blocks[i].y = y;
		}
		
		SoundEffect.play();
		
	}//def reconfigure shape 
	
	
	
	public void rotateRight() {
		int orgCycle = rotationCycle;
		rotationCycle++;
		SoundEffect.setFile(Sound.rotateRight);
		reconfigureShape(orgCycle);	
	}//def rotateRight
	
	
	public void rotateLeft() {
		int orgCycle = rotationCycle;
		rotationCycle--;
		SoundEffect.setFile(Sound.rotateLeft);
		reconfigureShape(orgCycle);
	}
	
	
	
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
			}//if	
		}//for
		
		
		//check for qualifying fully filled row coordinates
		for(int i = 0; i < toBeClearedRowCords.size(); i++) {
			int currYCord = toBeClearedRowCords.get(i);
			
			//check for empty blocks in the matrix's row
			boolean rowFullyBlocked = true;
			
			for(int x = 0; x < grid.width; x++) {
				if(grid.matrix[currYCord][x].type == BlockType.EMPTY) {
					rowFullyBlocked = false;
					break;
				}//if
			}//def for
			
			if(!rowFullyBlocked) {
				toBeClearedRowCords.remove(i);
				i--;
			}
		}//for
		
		if(!(toBeClearedRowCords.size() > 0)) {
			SoundEffect.setFile(Sound.setBlock);
			SoundEffect.play();
		}
		else if(toBeClearedRowCords.size() > 0) {
			grid.rowClearing = true;
			
			
			if(toBeClearedRowCords.size() >= 4) {
				SoundEffect.setFile(Sound.clearTetris);
			}else {
				SoundEffect.setFile(Sound.clearRow);
			}
			
			SoundEffect.play();
			
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
		
		grid.checkGameOver();
		
	}//def setBlock
	
	
	public void setColor(Color newColor){
		for(int i = 0; i < blocks.length; i++) {
			blocks[i].color = newColor;
		}
	}
	
	

	
}//class