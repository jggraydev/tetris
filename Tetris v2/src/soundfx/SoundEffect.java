package soundfx;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;



public class SoundEffect {
	public static Clip clip;
	
	public static boolean startGameTrigger = false; 
	
	public static void setFile(String soundFileName) {
		try{
			File file = new File(soundFileName);
			AudioInputStream sound01 = AudioSystem.getAudioInputStream(file);
			clip = AudioSystem.getClip();
			clip.open(sound01);
			
			
			FloatControl gainControl = 
			    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-28.0f); 
			
		}catch(Exception e){
			System.out.println("Exception thrown from filename " + soundFileName.toString());
			e.printStackTrace();
		}//try catch
		
	}//def setFile
	
	public static void play() {
		clip.setFramePosition(0);
		clip.start();
	}
}//class
