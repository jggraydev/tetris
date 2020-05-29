import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import Shapes.Shape;
import Shapes.Shape.ShapeConfig;
import input.Keyboard;

public class Game extends Canvas implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int scale = 3;
	public static int width = 150;
	public static int height = width * 16 / 9;
	public static String title = "Tetris v2";
	
	public static Color backgroundColor = new Color(40, 40, 40);
	
	public Thread thread;
	public JFrame frame;
	public boolean running = false;
	
	public Grid master;
	public Keyboard key;
	
	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		
		frame = new JFrame();
		frame.setTitle(title);
		
		key = new Keyboard();
		addKeyListener(key);
		
		master = new Grid(key);
		master.activeShape = new Shape(ShapeConfig.SQUARE, Grid.width, Grid.height);

	}
	
	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		long currTime = System.nanoTime();
		long timeAtLastUpdate = currTime;
		long timeAtLastUpdateCheck = currTime;
		
		int fpsCounter = 0;
		int updateCounter = 0;
		
		int tickRate = 60;
		int nanoSecondsPerSec = 1000000000;
		double timeBetweenUpdates = nanoSecondsPerSec / (double)tickRate;
		
		
		requestFocus();
		
		while(running) {
			currTime = System.nanoTime();
			
			if(currTime - timeAtLastUpdate > timeBetweenUpdates) {
				update();
				updateCounter++;
				timeAtLastUpdate = currTime;
			}
			
			if(currTime - timeAtLastUpdateCheck > nanoSecondsPerSec) {
				frame.setTitle(title + "  |  fps: " + fpsCounter + ", updates: " + updateCounter);
				updateCounter = 0;
				fpsCounter = 0;
				timeAtLastUpdateCheck = currTime;
			}
			
			fpsCounter++;
			render();
		}//def while running
	}//def run
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		// RENDER GRID 
		master.renderBlocks(g);
		
		
		g.dispose();
		bs.show();
		
	}//def render
	
	
	// KEY BINDINGS
	public void update() {
		
		master.update();

		
		
		
	}//def update
	
	
	public static void main(String[] args) {
		Game game = new Game();
		
		game.frame.setResizable(false);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setLocationRelativeTo(null);
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setVisible(true);
		
		game.start();
	}//main
}//class
