import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//Laser Class
public class LaserObstacle {

	//Variables
	private int x; 
	private int y; 
	private int width; 
	private int height; 
	private int speed; 
	private int direction;
	BufferedImage leftImage;
	BufferedImage rightImage;

	//Description: Constructor
	//Parameters: x,y,width,speed (useless), width, direction of movement
	//return: none
	public LaserObstacle(int x, int y, int xspeed, int width, int direction) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = 30;
		this.speed = speed;
		this.direction = direction;

		//Get images
		try {
			leftImage = ImageIO.read(new File("leftLaserPointer.png"));
			rightImage = ImageIO.read(new File("rightLaserPointer.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//Description: Move the laser down (or up depending on direction)
	//Parameters: none
	//return: none
	public void move() {
		if (direction < 0){
			y -= 2;
		}
		else if (direction > 0) {
			y += 2;
		}
		else {
			y += 0;
		}
	}

	//Description: Checks if out of bounds
	//Parameters: screen parameters
	//return: true or false
	public boolean isOutOfBounds(int screenHeight) {
		return y > screenHeight || y < 0;
	}

	//Description: Check collision with player
	//Parameters: hitbox
	//return: true or false
	public boolean checkCollision(Rectangle playerBounds) {
		return getBounds().intersects(playerBounds);
	}


	//Description: Draws laser 
	//Parameters: Graphics g
	//return: none
	public void draw(Graphics g) {
		g.setColor(Color.PINK);
		g.fillRect(x, y, width, height);

		g.setColor(Color.RED);
		g.fillRect(x, y+5, width, height-10);

		g.drawImage(leftImage, x, y-20, 80, 60, null);
		g.drawImage(rightImage, width-80, y-20, 80, 60, null);
	}

	//Description: Gets hitbox
	//Parameters: none
	//return: rectangle hitbox
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
}

