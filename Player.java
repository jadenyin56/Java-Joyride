import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Player {

	// Character Variables
	private int x;
	private int y; 
	private int width; 
	private int height;
	private double yVel;
	private double gravity; 
	private boolean airborne;
	private boolean jetpackActive;

	// Jetpack properties
	private double jetpackPower;
	private double fuel;        
	private double maxFuel;     

	// Profile properties
	private String name;
	private int highScore;
	private int currentScore;
	private String skin; 

	//Coin things
	private int totalCoins;
	private int currentCoins;

	// Base fireball image (didnt use)
	Image playerImage; 
	BufferedImage jetpackImage;

	//Description: Constructor for player
	//Parameters: name, starting position, size of player, highscore, and total number of coins
	//return: none
	public Player(String name, int startX, int startY, int width, int height, int highScore, int numCoins) {

		//Initialize Everything
		this.name = name;
		this.x = startX;
		this.y = startY;
		this.width = width;
		this.height = height;


		this.yVel = 0;
		this.gravity = 0.3;
		this.jetpackPower = 1.1;
		this.maxFuel = 200;
		this.fuel = maxFuel;
		this.airborne = false;


		this.highScore = highScore;
		this.currentScore = 0;
		//Did not use this
		this.skin = "default"; 

		this.totalCoins = numCoins;
		this.currentCoins = 0;

		jetpackActive = false;

		//Did not use this either
		try {
			playerImage = Toolkit.getDefaultToolkit().getImage("resizedAnimatedPlayer.gif");
			jetpackImage = ImageIO.read(new File("resizedJetpack.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Description: Movement logic, including jetpack usage
	//Parameters: User input
	//Return: none
	public void move(boolean activateJetpack) {

		// Jetpack logic
		jetpackActive = activateJetpack; // Track if the jetpack is active

		// Jetpack logic
		if (activateJetpack) {
			// Reduce falling or ascend
			yVel -= jetpackPower; 
			// Using the fuel
			fuel -= 0.5;
		} 

		// Apply gravity
		else {
			yVel += gravity;
		}
		// Update position
		y += yVel;

		stayInbounds();
		addScore();

	}

	//Desc: Makes player stay in bounds
	//Para: None
	//return: none
	public void stayInbounds() {
		// Prevent player from falling out of bounds
		if (y > 530) { // Assuming ground level is at y = 500
			y = 530;
			airborne = false;
			yVel = 0;
		} else if (y < 50) {
			y = 50; // Ceiling
			yVel = 0;
		}
	}

	//Desc: Adds score
	//Para: None
	//return: none
	public void addScore() {
		// Increment score
		currentScore++;
		if (currentScore > highScore) {
			highScore = currentScore;
		}
	}

	// Desc: Refuel jetpack when grounded
	//Para: None
	//return: none
	public void refuel() {
		if (!airborne) {
			fuel = maxFuel;
		}
	}

	//Desc: Draws the player
	//Para: None
	//return: none
	public void draw(Graphics g) {

		// If the jetpack is active, draw fire underneath
		if (jetpackActive && fuel > 0) {
			g.setColor(Color.ORANGE); // Flame base
			g.fillOval(x-1, y + height-5, 25, height);
			g.setColor(Color.RED); // Inner flame
			g.fillOval(x-1 + 6, y + height, 12, height - 10);
		}

		//Player Image
		g.drawImage(playerImage, x, y, width, height, null);

		//Player Image
		g.drawImage(jetpackImage, x+10, y+10, 30, 40, null);

		// Draw the jetpack fuel bar
		g.setColor(Color.GREEN);
		g.fillRect(20, 40, (int) (fuel / maxFuel * 100), 10);

		// Display score and high score
		g.setColor(Color.BLACK);
		g.drawString("Score: " + currentScore, 20, 20);
		g.drawString("High Score: " + highScore, 20, 35);
	}


	//Desc: Gets hitbox
	//Para: None
	//return: rectangle hitbox
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	//Getters and setters

	public String getSkin() {
		return skin;
	}

	public double getFuel() {
		return fuel;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHighScore() {
		return highScore;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int score) {
		this.currentScore = score;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public void setStartY(int y) {
		this.y = y;
	}

	public void setTotalCoins(int totalCoins) {
		this.totalCoins = totalCoins;
	}

	public void setPlayerName(String name) {
		this.name = name;
	}

	public void resetScore() {
		currentScore = 0;
	}
}
