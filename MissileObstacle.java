import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//Missile Class
public class MissileObstacle {


	//Variables
	private int x;
	private int y; 
	private int width; 
	private int height; 
	private int speed;
	private Rectangle bounds;
	BufferedImage missileImage;
	BufferedImage missileFire;

	//Description: Constructor
	//Parameters: x,y,width,height,speed
	//return: none
	public MissileObstacle(int x, int y, int width, int height, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.bounds = new Rectangle(x, y, width, height);

		//Load Images
		try {
			missileImage = ImageIO.read(new File("missileFire.png"));
			missileFire = ImageIO.read(new File("realMissile.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Description: Move the missile
	//Parameters: none
	//return: none
	public void move() {
		x -= speed;
		bounds.setLocation(x, y);
	}

	//Description: Checks bounds
	//Parameters: screenwidth
	//return: boolean
	public boolean isOutOfBounds(int screenWidth) {
		return x + width < 0;
	}

	//Description: Checks collision with player
	//Parameters: none
	//return: boolean
	public boolean checkCollision(Rectangle playerBounds) {
		return bounds.intersects(playerBounds);
	}

	//Description: Draws the missile
	//Parameters: none
	//return: none
	public void draw(Graphics g) {
		g.drawImage(missileImage, x, y, 70, 70, null);
		g.drawImage(missileFire, x + 65, y, 70, 70, null); 
	}

	////Description: Gets the hitbox
	//Parameters: none
	//return: Rectangle
	public Rectangle getBounds() {
		return bounds;
	}
}
