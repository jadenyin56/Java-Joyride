import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//Coin Class
//Values and Positions of Coins
public class Coin {

	//Variables
	private int x;
	private int y;
	private int value;
	private int width = 20;
	private int height = 20; 

	//Images for each value of coin
	private static Image[] coinImages;


	//Description: Construction
	//Parameters: x,y,value of coin
	//return: none
	public Coin(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.value = value; 

		coinImages = new Image[3]; 

		// Load images once when the class is loaded
		try {
			coinImages[0] = Toolkit.getDefaultToolkit().getImage("coin1.gif");
			coinImages[1] = Toolkit.getDefaultToolkit().getImage("coin2.gif");
			coinImages[2] = Toolkit.getDefaultToolkit().getImage("coin3.gif");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load coin images.");
		}
	}

	//Getter
	public int getValue() {
		return value;
	}

	//Description: Gets hitbox
	//Parameters: none
	//return: rectangle hitbox
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	//Description: Moves the coins with regard to the screen
	//Parameters: speed
	//return: none
	public void move(int speed) {
		x -= speed; // Move coin left with the screen
	}

	//Description: Draws the coins so i dont need to in paint component
	//Parameters: Graphics g
	//return: none
	public void draw(Graphics g) {
		Image coinImage = null;

		// Select the correct image based on the coin value
		if (value == 1) {
			coinImage = coinImages[2];
		} else if (value == 2) {
			coinImage = coinImages[1];
		} else if (value == 3) {
			coinImage = coinImages[0];
		}

		// Draw the coin image if it's loaded
		if (coinImage != null) {
			g.drawImage(coinImage, x, y, width, height, null);
		} else {
			// If image is not available, draw a placeholder circle
			g.setColor(Color.YELLOW);
			g.fillOval(x, y, width, height);
		}
	}
}
