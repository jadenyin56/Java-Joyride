import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//Missile Pattern Class
public class MissilePattern {

	//Variables
	private int screenWidth;
	private int screenHeight;
	private int baseWarningDuration; 
	private int minWarningDuration;  
	private long lastMissileTime;    
	private int difficultyScalingFactor; 

	//Description: Constructor
	//Parameters: screen parameters, duration, difficulty thing
	//return: none
	public MissilePattern(int screenWidth, int screenHeight, int baseWarningDuration, int minWarningDuration, int difficultyScalingFactor) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.baseWarningDuration = baseWarningDuration;
		this.minWarningDuration = minWarningDuration;
		this.lastMissileTime = System.currentTimeMillis();
		this.difficultyScalingFactor = difficultyScalingFactor;
	}

	//Description: Gets position of missile (y position)
	//Parameters: players y coordinates, current time
	//return: integer array
	public int[] generateMissilePosition(int playerY, int score) {

		// Adjust missile's vertical position to target player more closely as score increases
		int maxDeviation = Math.max(100 - score / 10, 10); 
		int y = playerY + (int)(Math.random()*(maxDeviation * 2)) - maxDeviation;

		// Ensure y is within screen bounds
		y = Math.max(25, Math.min(screenHeight - 50, y));
		return new int[]{screenWidth - 50, y};
	}

	//Description: Checks if missile is ready
	//Parameters: current time
	//return: true or false
	public boolean isTimeForMissile(int score) {
		// Decrease warning duration as the score increases
		int adjustedWarningDuration = Math.max(minWarningDuration, baseWarningDuration - (score / difficultyScalingFactor));
		return System.currentTimeMillis() - lastMissileTime >= adjustedWarningDuration;
	}

	//Description: updates missile time
	//Parameters: none
	//return: none
	public void updateLastMissileTime() {
		lastMissileTime = System.currentTimeMillis();
	}
}
