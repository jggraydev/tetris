package Shapes;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import Shapes.Block.BlockType;
import Shapes.Shape.ShapeConfig;
import input.Keyboard;
import soundfx.Sound;
import soundfx.SoundEffect;


public class Grid {
	public int width = 10;
	public int height = 20;
	public int heightLeeway = 2;
	public int safeZoneHeight = 6;
	public int heightTrue = height + safeZoneHeight;
	public boolean running;
	
	public int screenWidth;
	public int screenHeight;
	
	public Shape activeShape;
	
	public int blockGutter = 6; // PIXELS between each block
	public int blockWidth = 25; 
	public int blockHeight = blockWidth;
	public int gridInitOffsetX = 30; 
	public int gridInitOffsetY = 30;
	
	private int gridPlaneAlphaMax = 100;
	private int gridPlaneAlphaStart = 0;
	private int gridPlaneAlpha = gridPlaneAlphaStart;
	private int gridPlaneAlphaParity = 50;
	private Color gridPlaneColor = new Color(255, 255, 255, gridPlaneAlpha);
	public int gamePadding = 30;
	public Color textColor = new Color(255, 255, 255);
	
	
	public Keyboard key;
	public Block[][] matrix = new Block[heightTrue][width];
	// intput delay for moving block over;
	private int inputDelay = 8;
	private int inputDelayCounterLeft = 0;
	private int inputDelayCounterRight = 0;
	
	
	// block queue
	public int shapeQueueSizeLimit = 7;
	public int shapeQueueSizeTrigger = 3;
	public ArrayList<Shape> shapeQueue = new ArrayList<Shape>();
	private int blockWidthQueue = 10;
	private int blockHeightQueue = blockWidthQueue;
	private int blockGutterQueue = 3;
	
	
	// tick speed
	public int tickDownCounterSpeed = 48;
	public int[] tickDownSpeeds = {48, 43, 38, 33, 28, 23, 18, 13, 8, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1}; // based on the NES tetris
	public int tickDownIndex = 0;
	public int tickDownRate = 1;
	public int tickRate = 60;
	public int nanoSecondsPerSec = 1000000000;
	public double timeBetweenTickDowns = nanoSecondsPerSec / tickDownRate;

	
	// row clearing
	public boolean rowClearing = false;
	public Block[][] blockClearer;
	public int blockClearerColIndex = width - 1;
	public int clearRateA = 12;
	public int clearRateB = 3;
	public int clearRateIndex = 0;
	
	
	// score ui
	public long score = 0;
	// 1 row = 100
	// 4 rows (aka tetris) = 800
	// subsequent tetrises = 1200
	int clearedLinesCount = 0;
	public boolean lastClearTetris = false;
	public boolean currentClearTetris = false;
	private int level = 0;
	private int uixPadding = 30;
	private int uixMargin = gridInitOffsetX + (blockWidth + blockGutter) * width + uixPadding; // (gamePadding + 100);
	private int uiyMargin = gridInitOffsetY; //screenHeight - 200;
	
	
	// tick down rate bar
	private int tdBarWidth = 9;
	private int tdBarHeight = 106;
	private int tdBarPadding = 6;
	private int tdBarHeightInner = tdBarHeight - tdBarPadding;
	private int tdBarWidthInner = tdBarWidth - tdBarPadding;
	private Color tdBarColor = new Color(100, 100, 100);
	private Color tdBarColorInner = new Color(255, 255, 255);
	private Color tdBarColorProgress = new Color(255, 0, 0);
	
	
	// line drought counter
	private int lineDroughtCount = 0;
	private int lineDroughtIndicator = 5;
	private Shape lineDroughtShape = new Shape(ShapeConfig.LINE, this);
	private int lineDroughtAlphaVal = 0;
	private int lineDroughtAlphaParityStart = 2;
	private int lineDroughtAlphaParity = lineDroughtAlphaParityStart;
	private int lineDroughtBlockWidth = 11;
	private int lineDroughtBlockHeight = lineDroughtBlockWidth;
	private int lineDroughtBlockGutter = 3;
	private int ldfpMillerA = 2;
	private int ldfpMillerB = 0;
	private int ldfpMillerBStart = 0;
	
	
	// game over components
	public boolean isGameOver = false;
	private int escCounterTrigger = 100;
	private int escCounter = 0;
	private int escMsgAlphaVal = 50;
	private final int escMsgFlashParity = 4;
	private int escMsgFlashCounter = escMsgFlashParity;
	private Color escMsgTextColor = new Color(255, 0, 0, escMsgAlphaVal); // Red + alpha
	
	public Color gameOverBoxColor = new Color(205, 55, 55);
	public int gameOverBoxHeight = 120;
	public int gameOverBoxWidth = 300;
	private int gameOverFontSize = 30;
	
	
	// FAULT LINE
	public Color faultLineColorGreen = new Color(30, 175, 20);
	public Color faultLineColor = faultLineColorGreen;
	
	public int faultBlockColorVal = 255;
	public int faultBlockFlashParity = 25;
	
	public int faultLineColorVal = 0;
	public int faultLineDangerParity = 15;
	public Color faultLineColorDanger = new Color(faultLineColorVal, 0, 0);
	public int faultLineFlashParity = 15;
	public int faultLineThickness = 1;
	public boolean gameOverFirstTriggered = false;
	public int faultLineDangerHeight = 6;
	
	
	
	
	
	//sound effects
	public boolean gameStartTrigger = false;
	public long gameStartTriggerCounter = 0;
	
	public Grid(Keyboard keyArg, int w, int h) {
		key = keyArg;
		for(int y = 0; y < heightTrue; y++) {
			for(int x = 0; x < width; x++) {
				matrix[y][x] = new Block(x, y, ShapeConfig.EMPTY);
			}//for x
		}//for y
		
		screenWidth = w;
		screenHeight = h;
		
		
		//set random shapes into queue
		for(int i = 0; i < shapeQueue.size(); i++) {
			//shapeQueue[i] = getNewShape();
		}//for
	}//constructor
	
	
	public void render(Graphics g) {
		renderBlocks(g);
		renderTickDownTimer(g);
		renderScoreCounters(g);
		renderShapeQueue(g);
		gameOverMessage(g);
		
	}
	
	
	public double getTimeBetweenTickDowns() {
		return (nanoSecondsPerSec) * ((double)tickDownCounterSpeed / tickRate);
	}
	
	
	public void update() {		
		// handle fault line danger color
		boolean faultLineDanger = false;
		
		//check if faultline needs to blink
		for(int y = height - faultLineDangerHeight; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Block currBlock = matrix[y][x];
				if(currBlock.type != BlockType.EMPTY) {
					faultLineDanger = true;
					break;
				}
			}// for x
		}// for y
		
		
		// update fault line color
		if(faultLineDanger) {
			faultLineColorVal += faultLineDangerParity;
			if(faultLineColorVal > 255) {
				faultLineColorVal = 255;
				faultLineDangerParity *= -1;
			}else if(faultLineColorVal < 0) {
				faultLineColorVal = 0;
				faultLineDangerParity *= -1;
			}
			faultLineColor = new Color(faultLineColorVal, 0, 0);
		}else {
			faultLineColor = faultLineColorGreen;
		}
		
		
		addToShapeQueue();
		
		
		// quit game if escape key held
		if(key.keys[KeyEvent.VK_ESCAPE]) {
			escCounter++;
			escMsgAlphaVal += escMsgFlashCounter;
			
			if(escMsgAlphaVal > 255) {
				escMsgAlphaVal = 255;
				escMsgFlashCounter *= -1;
			}else if(escMsgAlphaVal <= 0) {
				escMsgAlphaVal = 0;
				escMsgFlashCounter *= -1;
			}
			
			
			
			
			if(escCounter >= escCounterTrigger) {
				// trigger game close
			}
		}else {
			escCounter = 0;
			escMsgAlphaVal = 50;
			escMsgFlashCounter = escMsgFlashParity;
		}
		
		
		// update flashing shape for the line drought indicator
		if(lineDroughtCount >= lineDroughtIndicator) {
			lineDroughtAlphaVal += lineDroughtAlphaParity;
			
			if(lineDroughtAlphaVal > 255) {
				lineDroughtAlphaVal = 255;
				lineDroughtAlphaParity *= -1;
			}else if(lineDroughtAlphaVal < 0) {
				lineDroughtAlphaVal = 0;
				lineDroughtAlphaParity *= -1;
			}
			
		}else {
			lineDroughtAlphaVal = 0;
			lineDroughtAlphaParity = lineDroughtAlphaParityStart;
		}
		
		
		// divert from updating other game state until clearRow() has cleared it's block queue
		if(rowClearing) {
			clearRow();
			return;
		}

		
		//game over state
		if(isGameOver) {
			if(!gameOverFirstTriggered) {
				SoundEffect.setFile(Sound.gameOver);
				SoundEffect.play();
				gameOverFirstTriggered = true;
			}
			
			//change color of faulting blocks inorder to flash
			// render safe zone blocks
			Block currBlock;
			
			if(faultBlockColorVal >= 255) {
				faultBlockColorVal = 255;
				faultBlockFlashParity *= -1;
			} else if(faultBlockColorVal <= 0){
				faultBlockColorVal = 0;
				faultBlockFlashParity *= -1;
			}
			
			for(int y = heightTrue - 1; y >= height; y--) {
				for(int x = 0; x < width; x++) {
					currBlock = matrix[y][x];
					
					if(currBlock.type == BlockType.SET) {
						int newRange = faultBlockColorVal;
						currBlock.color = new Color(newRange, newRange, newRange);
						
						
						
					}//if block type
				}//for x
			}//for y
			
			faultBlockColorVal += faultBlockFlashParity ;	
			return;
		}
		
		
		// get new active shape if there is none
		if(activeShape == null) {
			activeShape = shapeQueue.get(0); 
			shapeQueue.remove(0);
			//Shape.getNewShape(this);
			if(activeShape.config != ShapeConfig.LINE) {
				lineDroughtCount++;
				if(lineDroughtAlphaParity > 0) {
					lineDroughtAlphaParity++;
				}else {
					lineDroughtAlphaParity--;
				}
			}else {
				lineDroughtCount = 0;
			}
			activeShape.justSpawned = true;
			return;
		}
		
		// updates delta time of activeShape
		activeShape.currTime = System.nanoTime();
		
		
		// ======================================================
		// === KEY BINDINGS =====================================
		// ======================================================
		
		//LEFT
		if(key.keys[KeyEvent.VK_LEFT]) {
			//INPUT LIMITER
			if(!key.keysTrigger[KeyEvent.VK_LEFT]) {
				inputDelayCounterLeft = 0;
				key.keysTrigger[KeyEvent.VK_LEFT] = true;
				moveShapeLeft();
			}else {
				inputDelayCounterLeft++;
				if(inputDelayCounterLeft >= inputDelay) {
					key.keysTrigger[KeyEvent.VK_LEFT] = false;
				}
			}
		//RIGHT	
		}else if(key.keys[KeyEvent.VK_RIGHT]) {
			//INPUT LIMITER
			if(!key.keysTrigger[KeyEvent.VK_RIGHT]) {
				inputDelayCounterRight = 0;
				key.keysTrigger[KeyEvent.VK_RIGHT] = true;
				moveShapeRight();
			}else {
				inputDelayCounterRight++;
				if(inputDelayCounterRight >= inputDelay) {
					key.keysTrigger[KeyEvent.VK_RIGHT] = false;
				}
			}
		}// if/else
		
		// rotate left/right
		if(key.keys[KeyEvent.VK_Z]) {
			if(!key.keysTrigger[KeyEvent.VK_Z]) {
				key.keysTrigger[KeyEvent.VK_Z] = true;
				activeShape.rotateLeft();
			}//if keysTrigger
		}else if(key.keys[KeyEvent.VK_X]) {
			if(!key.keysTrigger[KeyEvent.VK_X]) {
				key.keysTrigger[KeyEvent.VK_X] = true;
				activeShape.rotateRight();
			}//if keysTrigger
		}
		
		
		// soft drop
		if(key.keys[KeyEvent.VK_DOWN]) {
			activeShape.tickDown();
		}
		
		
		//test sound effect
		if(key.keys[KeyEvent.VK_SPACE]) {

			if(!key.keysTrigger[KeyEvent.VK_SPACE]) {
				key.keysTrigger[KeyEvent.VK_SPACE] = true;
				System.out.println("KEY INPUT: space triggered");
				SoundEffect.setFile(Sound.gameOver);
				SoundEffect.play();
			}
		}
		
		// ======= check tick down ==============================
		if(activeShape != null) {
			if(activeShape.justSpawned) {
				activeShape.justSpawned = false;
				activeShape.currTime = System.nanoTime();
				activeShape.timeAtLastTickDown = activeShape.currTime;
				return;
			}
			
			if(activeShape.currTime - activeShape.timeAtLastTickDown > getTimeBetweenTickDowns()) {
				activeShape.tickDown();
			}
		}// if activeShape exists
	}// || || || === def update ==============================================================================================
	
	
	// render score / line count / drought counter
	public void renderScoreCounters(Graphics g) {
		Color textColor = new Color(255, 255, 255);
		g.setColor(textColor);
		String textToRender = "Score: " + score;
		int y = uiyMargin;
		int x = uixMargin;
				 
		g.drawString(textToRender, x, y);
		
		textToRender = "Line count: " + clearedLinesCount;
		
		y += 15;
		
		g.drawString(textToRender, x, y);
		
		textToRender = "Level: " + level;
		
		y += 25;
		
		g.drawString(textToRender, x, y);
		
		y += 200;
		
		
		boolean lineInQueue = false;
		for(int i = 0; i < shapeQueue.size() && i < shapeQueueSizeTrigger; i++) {
			Shape currShape = shapeQueue.get(i);
			if(currShape.config == ShapeConfig.LINE) {
				lineInQueue = true;
				break;
			}//if
		}//for
		
		if(lineDroughtCount >= lineDroughtIndicator && !lineInQueue) {
			textToRender = "Drought: " + lineDroughtCount;
			g.drawString(textToRender, x, y);
			
			lineDroughtShape.setColor(new Color(lineDroughtAlphaVal, 0, 0));
			
			y += 10;
			
			g.setColor(lineDroughtShape.blocks[0].color);
			for(int i = 0; i < lineDroughtShape.blocks.length; i++) {
				g.fillRect(x, y, lineDroughtBlockWidth, lineDroughtBlockHeight);
				x += lineDroughtBlockGutter + lineDroughtBlockWidth;
			}//for
			
		}//if

		
		
		//IF escape key is being held
		if(key.keys[KeyEvent.VK_ESCAPE]) {
			int uix = screenWidth - (gamePadding + 100);
			int uiy = 500;
			
			textToRender = "QUITTING GAME...";
			escMsgTextColor = new Color(255, 0, 0, escMsgAlphaVal); 
			g.setColor(escMsgTextColor);
			g.drawString(textToRender, uix, uiy);
			
			uiy += 30;
			
			textToRender = "Programmed by";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 15;
			
			textToRender = "Jonathan Gray";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 15;
			
			textToRender = "jggraydev";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 30;
			
			/*
			SOUND FILES BY
		 	Jalastram (Jesus Lastra)
		 	jalastram@gmail.com 
			*/
			
			textToRender = "Sound effects by";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 15;
			textToRender = "jalastram";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 15;
			textToRender = "(Jesus Lastra)";
			g.drawString(textToRender, uix, uiy);
			
			uiy += 15;
			textToRender = "jalastram@gmail.com ";
			g.drawString(textToRender, uix, uiy);
			
			
		}//if
		
		
	}//def renderScoreCounters
	
	
	
	// render tick down gauge
	public void renderTickDownTimer(Graphics g) {
		long timeAtLastTickDown;
		long currTime;
		
		if(activeShape == null) {
			currTime = System.nanoTime();
			timeAtLastTickDown = currTime;
			
		}
		else {
			currTime = activeShape.currTime;
			timeAtLastTickDown = activeShape.timeAtLastTickDown;
		}
		
		long timeDelta = currTime - timeAtLastTickDown;
		double percentagePrecision = timeDelta / getTimeBetweenTickDowns();
		
		int percentageFillBlock = (int)(percentagePrecision * tdBarHeightInner);
		
		
		g.setColor(tdBarColor);
		int x = uixMargin;
		int y = screenHeight - 180;
		g.fillRect(x, y, tdBarWidth, tdBarHeight);
		g.setColor(tdBarColorInner);
		x += (tdBarPadding / 2);
		y += (tdBarPadding / 2);
		g.fillRect(x, y, tdBarWidthInner, tdBarHeightInner);
		
		g.setColor(tdBarColorProgress);
		y += tdBarHeightInner;
		y -= percentageFillBlock;
		g.fillRect(x, y, tdBarWidthInner, percentageFillBlock);
		
	}
	
	
	
	// renders set blocks and active block
	public void renderBlocks(Graphics g) {
		Block currBlock;
		int yCurse = gridInitOffsetY;
		int xCurse = gridInitOffsetX; 
		
		g.setColor(gridPlaneColor);
		int boxWidth = width * (blockWidth + blockGutter) - blockGutter;
		int boxHeight = heightTrue * (blockHeight + blockGutter) - blockGutter;
		g.fillRect(xCurse, yCurse, boxWidth, boxHeight);
		
		// render safe zone blocks
		for(int y = heightTrue - 1; y >= height; y--) {
			for(int x = 0; x < width; x++) {
				currBlock = matrix[y][x];
				
				g.setColor(currBlock.color);
				
				
				if(currBlock.type == BlockType.EMPTY) {
					g.setColor(new Color(130, 130, 130, 100));
				}
				
				g.fillRect(xCurse, yCurse, blockWidth, blockHeight);	
				
				xCurse += (blockWidth + blockGutter);
			}//for x
			xCurse = gridInitOffsetX;
			yCurse += (blockHeight + blockGutter);
		}//for y
		
		
		// fault line is removed if game over
		if(!isGameOver) {
			int gridPixelWidth = (blockGutter + blockWidth) * width;
			
			g.setColor(faultLineColor);
			g.fillRect(xCurse, yCurse - (faultLineThickness / 2) - 2, gridPixelWidth, faultLineThickness);
		}
		
		
		// render grid zone blocks
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
				
				//g.setColor(textColor);
				//g.drawString(valString, xCurse, yCurse);
				
				xCurse += cx1;
				yCurse += cy1;
				
				xCurse += (blockWidth + blockGutter);
			}
			xCurse = gridInitOffsetX;
			yCurse += (blockHeight + blockGutter);
		}
		
		renderActiveShape(g);
	}//def renderBlocks
	
	
	//renders current shape
	public void renderActiveShape(Graphics g) {
		Block currBlock;
		int yCurse = gridInitOffsetY;
		int xCurse = gridInitOffsetX;
		
		if(activeShape != null) {
			for(int i = 0; i < activeShape.blocks.length; i++) {
				
				currBlock = activeShape.blocks[i];
				g.setColor(currBlock.color);
				
				xCurse = ((currBlock.x) * (blockWidth + blockGutter)) + gridInitOffsetX;
				yCurse = (((heightTrue - 1) - currBlock.y) * (blockHeight + blockGutter)) + gridInitOffsetY;
				
				g.fillRect(xCurse, yCurse, blockWidth, blockHeight);
				
			}//for 
		}
	}//def renderActiveShape
	
	
	// renders next shapes
	public void renderShapeQueue(Graphics g) {
		int yStart = uiyMargin + 90;
		int xStart = uixMargin + 30;
		
		int xCurse = xStart;
		int yCurse = yStart;
		
		/*
		if(shapeQueue.size() == 0) {
			return;
		}
		*/
		
		for(int i = 0; i < shapeQueue.size() && i < shapeQueueSizeTrigger; i++) {
			Shape currShape = shapeQueue.get(i);
			g.setColor(currShape.blocks[0].color);
			
			for(int k = 0; k < currShape.blocks.length; k++) {
				Block currBlock = currShape.blocks[k];
				int xCord = currBlock.x - (width / 2);
				int yCord = (currBlock.y - (height + heightLeeway)) * -1;
				
				int xDiff = xCord * (blockWidthQueue + blockGutterQueue);
				int yDiff = yCord * (blockHeightQueue + blockGutterQueue);
				g.fillRect(xCurse + xDiff, yCurse + yDiff, blockWidthQueue, blockHeightQueue);
			}
			
			xCurse = xStart;
			yCurse += 50;
		}//for

	}//def 
	
	
	// adds random shapes to queue if the number of shapes is less than a certain amount
	public void addToShapeQueue() {
		if(shapeQueue.size() < shapeQueueSizeTrigger) {
			while(shapeQueue.size() < shapeQueueSizeLimit) {
				shapeQueue.add(Shape.getNewShape(this));
			}
		}
	}
	
	
	public void moveShapeLeft() {
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
	
	
	// clears rows that are fully filled
	public void clearRow(){
		// controls rate at which the lines clear away
		if(clearRateIndex < clearRateA) {
			clearRateIndex += clearRateB;
			return;
		}else {
			clearRateIndex = 0;
		}//if/else
		
		int holdX;
		int holdY;
		
		int highestRowIndex = blockClearer[0][0].y;
		int lowestRowIndex = blockClearer[0][0].y;
		
		for(int rowIndex = 0; rowIndex < blockClearer.length; rowIndex++) {
			
			Block currBlock = blockClearer[rowIndex][blockClearerColIndex];
			holdX = currBlock.x;
			holdY = currBlock.y;
			currBlock = new Block(holdX, holdY, ShapeConfig.EMPTY);

			if(holdY > highestRowIndex) {
				highestRowIndex = holdY;
			}
			
			if(holdY < lowestRowIndex) {
				lowestRowIndex = holdY;
			}
			
			matrix[holdY][holdX] = currBlock;
		}//for
		
		// resets counter and triggers end of row clearing
		if(blockClearerColIndex == 0) {
			blockClearerColIndex = width - 1;
			rowClearing = false;
					
			incrementScore(blockClearer.length);
			
			shiftRowsDown(blockClearer);
			//blockClearer = null;

		} else {
			blockClearerColIndex--;
		}//if/else	
	}//def clearRow
	
	
	//shift rows down after they've been cleared
	public void shiftRowsDown(Block[][] blockArray) {
		int[] rowCords = new int[blockArray.length];
		
		for(int i = 0; i < blockArray.length;i++) {
			rowCords[i] = blockArray[i][0].y;
		}
		
		Arrays.sort(rowCords);
		
		for(int i = 0; i < rowCords.length; i++) {
			int shiftFactor = i;//this variable is needed to deal with the row shifting index needing to be reduced by 1 for each row that was already shifted down
			int y = rowCords[i] - shiftFactor;
			shiftRowDown(y);
		}
	}
	
	
	// pass in the index of the cleared row (y coordinate) to shift the above row down
	public void shiftRowDown(int rowIndex) {
		
		System.out.println("row being shifted down: " + rowIndex);
		for(int y = rowIndex; y < heightTrue - 2; y++) {
			
			int lowerRowIndex = y;
			int aboveRowIndex = lowerRowIndex + 1;
			
			for(int x = 0; x < width; x++) {
				// lower the y coordinate of the above row, then CHANGE the block in that matrix
				Block shiftingBlock = matrix[aboveRowIndex][x];
				shiftingBlock.y--;
				
				matrix[lowerRowIndex][x] = shiftingBlock; //matrix[aboveRowIndex][x];
				
				// the above shifted row should then become empty
				matrix[aboveRowIndex][x] = new Block(x, aboveRowIndex, ShapeConfig.EMPTY);
			}//for
		}//for
	}//def
	
	
	// increments score and line count / updates level
	public void incrementScore(int numRows) {
		int scoreAmount = 0;
		
		if(numRows == 4) {
			if(lastClearTetris) {
				scoreAmount += 400;
			}
			lastClearTetris = true;
			scoreAmount += 800;
		} else {
			lastClearTetris = false;
			if(numRows == 1) {
				scoreAmount += 100;
			}else if(numRows == 2) {
				scoreAmount += 200;
			}else if(numRows == 3) {
				scoreAmount += 300;
			}//else
		}//if lastClearTetris
		score += scoreAmount;
		clearedLinesCount += numRows;
		
		
		if(clearedLinesCount >= 280) {
			level = 29;
			tickDownIndex = 29;
		}else
		
		if(clearedLinesCount >= 180) {
			level = 19;
			tickDownIndex++;
		}else
			
		if(clearedLinesCount >= 130) {
			level = 14;
			tickDownRate++;
		}else
		if(clearedLinesCount >= 120) {
			level = 13;
			tickDownRate = 13;
		}else
		if(clearedLinesCount >= 110) {
			level = 12;
			tickDownRate = 12;
		}else
		
		if(clearedLinesCount >= 100) {
			level = 11;
			tickDownRate = 11;
		}else if(clearedLinesCount >= 90) {
			level = 10;
			tickDownRate = 10;
		}else if(clearedLinesCount >= 80) {
				level = 9;
				tickDownRate = 9;
		}else if(clearedLinesCount >= 70) {
			level = 8;
			tickDownRate = 8;
		}else if(clearedLinesCount >= 60) {
			level = 7;
			tickDownRate = 7;
		}
		else if(clearedLinesCount >= 50) {
			level = 6;
			tickDownRate = 6;
		}
		else if(clearedLinesCount >= 40) {
			level = 5;
			tickDownRate = 5;
		}
		else if(clearedLinesCount >= 30) {
			level = 4;
			tickDownRate = 4;
		}
		else if(clearedLinesCount >= 20) {
			level = 3;
			tickDownRate = 3;
		}
		else if(clearedLinesCount >= 10) {
			level = 2;
			tickDownRate = 2;
		}
		
		tickDownCounterSpeed = tickDownSpeeds[tickDownIndex];
	}//def incrementScore
	
	
	// triggers boolean for game over after a block is set
	public void checkGameOver() {
		// checks for blocks above the fault line
		for(int x = 0; x < width; x++) {
			Block currBlock = matrix[height][x];
			if(currBlock.type != BlockType.EMPTY) {
				isGameOver = true;
				System.out.println(">>>> Block detected in safe zone >> Game over");
				//break;
				return;
			}
		}//for
	}//def checkGameOver	
	
	
	// renders game over message
	public void gameOverMessage(Graphics g) {
		// spawns game over
		if(isGameOver) {
			g.setColor(gameOverBoxColor);
			
			int x = ((screenWidth / 2) - (gameOverBoxWidth / 2));
			int y = ((screenHeight / 2) - (gameOverBoxHeight / 2) - 100);
			g.fillRect(x, y, gameOverBoxWidth, gameOverBoxHeight);
			
			x += (gameOverBoxWidth / 4) - (gameOverFontSize / 2);
			y += gameOverBoxHeight / 2;
			
			g.setColor(textColor);
			
			g.setFont(new Font("Helvetica", Font.BOLD, gameOverFontSize)); 
			g.drawString("GAMEOVER", x, y);
		}//if
	}//def checkGameOver
	
}//class
