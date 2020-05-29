package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{
	public boolean[] keys = new boolean[250];
	public boolean[] keysTrigger = new boolean[250];
	
	
	public void keyPressed(KeyEvent e) { 
		keys[e.getKeyCode()] = true;
	}
	
	public void keyReleased(KeyEvent e) { 
		keys[e.getKeyCode()] = false;
		keysTrigger[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) { }
	
	
	public void update() {
		
		/*
		System.out.print("KEYS PRESSED:");
		for(int i = 0; i < keys.length; i++) {
			if(keys[i]) System.out.print(i + " ");
		}
		System.out.println();
		*/
	}
	
	public void gridTest() {
		System.out.println("Keyboard methdod called from grid");
	}
}
