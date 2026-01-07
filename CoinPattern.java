import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//CoinPattern Class
public class CoinPattern {

	//Arraylist of coins
	private ArrayList<Coin> coins;

	//Description: Constructor
	//Parameters: x pos, y pos, the pattern
	//return: none
	public CoinPattern(int x, int y, String pattern) {
		this.coins = new ArrayList<>();
		generatePattern(x, y, pattern);
	}

	//Description: Generates a coin pattern (6 different patterns)
	//Parameters: x pos, y pos, pattern string
	//return: none
	public void generatePattern(int x, int y, String pattern) {

		//Random values and random spacing
		int coinSpacing = (int)(Math.random()*(20)) + 40;
		int coinValue = (int)(Math.random()*(3)) + 1;

		//Line
		if (pattern.equalsIgnoreCase("line")) {
			for (int i = 0; i < 10; i++) {
				coins.add(new Coin(x + i * coinSpacing, y, coinValue));
			}
		} 


		//Triangle
		else if (pattern.equalsIgnoreCase("triangle")) {
			for (int row = 0; row < 5; row++) {
				for (int col = 0; col <= row; col++) {
					coins.add(new Coin(
							x + col * coinSpacing - (row * coinSpacing) / 2,
							y - row * coinSpacing,
							coinValue));
				}
			}
		} 

		//Cluster
		else if (pattern.equalsIgnoreCase("cluster")) {
			for (int i = 0; i < 10; i++) {
				int randomOffsetX = (int) (Math.random() * (80) - 40);
				int randomOffsetY = (int) (Math.random() * (80) - 40);
				coins.add(new Coin(
						x + randomOffsetX,
						y + randomOffsetY,
						coinValue));
			}
		}

		//Wave
		else if (pattern.equalsIgnoreCase("wave")) {
			for (int i = 0; i < 15; i++) {
				int waveHeight = 50; // Height of the wave
				coins.add(new Coin(
						x + i * coinSpacing,
						y + (int) (waveHeight * Math.sin(i * 0.5)),
						coinValue));
			}
		} 


		//Zigzag
		else if (pattern.equalsIgnoreCase("zigzag")) {
			for (int i = 0; i < 15; i++) {
				int direction = (i % 2 == 0) ? 1 : -1;
				coins.add(new Coin(
						x + i * coinSpacing,
						y + direction * 30,
						coinValue));
			}
		} 

		//Circle
		else if (pattern.equalsIgnoreCase("circle")) {
			int radius = 60;
			for (int i = 0; i < 12; i++) {
				double angle = 2 * Math.PI * i / 12;
				coins.add(new Coin(
						x + (int) (radius * Math.cos(angle)),
						y + (int) (radius * Math.sin(angle)),
						coinValue));
			}
		} 

		//Spirtal
		else if (pattern.equalsIgnoreCase("spiral")) {
			int spiralRadius = 10;
			for (int i = 0; i < 20; i++) {
				double angle = 2 * Math.PI * i / 6;
				coins.add(new Coin(
						x + (int) (spiralRadius * i * Math.cos(angle)),
						y + (int) (spiralRadius * i * Math.sin(angle)),
						coinValue));
			}
		} 

		//In case smth happens ig
		else {
			throw new IllegalArgumentException("Unknown pattern: " + pattern);
		}
	}

	//Returns the coin arraylist (getter)
	public ArrayList<Coin> getCoins() {
		return coins;
	}

	//Description: Moves the coins with regard to the screen
	//Parameters: speed
	//return: none
	public void update(int speed) {
		for (int i = coins.size() - 1; i >= 0; i--) {
			Coin coin = coins.get(i);
			// Move coin left
			coin.move(speed); 
			if (coin.getBounds().x + coin.getBounds().width < 0) {
				// Remove coins that move off-screen
				coins.remove(i); 
			}
		}
	}

	//Description: Draws the coins so i dont need to in paint component
	//Parameters: Graphics g
	//return: none
	public void draw(Graphics g) {
		for (Coin coin : coins) {
			coin.draw(g);
		}
	}
}
