import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

//Fireball Class
public class FireballObstacle {

	//Variables
	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;


	//Description: Construction
	//Parameters: x,y,width,height,speed
	//return: none
	public FireballObstacle(int x, int y, int width, int height, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;


	}

	//Description: Updates position of fireball
	//Parameters: speed
	//return: none
	public void update(int speed) {
		x -= speed;
	}

	//Description: Draws fireball 
	//Parameters: Graphics g
	//return: none
	public void draw(Graphics g) {

		//Rectangles
		g.setColor(Color.ORANGE);
		g.fillRect(x-5, y-5, width+10, height+10);
		g.setColor(Color.YELLOW);
		g.fillRect(x, y, width, height);

		// Draw circles on each side of the rectangle
		int circleRadius = 10; 
		int circleDiameter = circleRadius * 2;

		// Calculate the circle positions
		int leftX = x - circleRadius; 
		int rightX = x + width - circleRadius; 
		int topY = y - circleRadius;
		int bottomY = y + height - circleRadius; 

		int middleLeftX = x + circleRadius; 
		int middleRightX = x + width - circleRadius * 3; 
		int middleTopY = y + circleRadius; 
		int middleBottomY = y + height - circleRadius * 3; 

		// Draw the outer circles (orange)
		g.setColor(Color.GRAY);
		g.fillOval(leftX, y + height / 2 - circleRadius, circleDiameter, circleDiameter); 
		g.fillOval(rightX, y + height / 2 - circleRadius, circleDiameter, circleDiameter); 
		g.fillOval(x + width / 2 - circleRadius, topY, circleDiameter, circleDiameter); 
		g.fillOval(x + width / 2 - circleRadius, bottomY, circleDiameter, circleDiameter); 

		// Draw the middle circles (gray)
		g.setColor(Color.ORANGE);
		g.fillOval(middleLeftX, y + height / 2 - circleRadius, circleDiameter, circleDiameter); 
		g.fillOval(middleRightX, y + height / 2 - circleRadius, circleDiameter, circleDiameter); 
		g.fillOval(x + width / 2 - circleRadius, middleTopY, circleDiameter, circleDiameter); 
		g.fillOval(x + width / 2 - circleRadius, middleBottomY, circleDiameter, circleDiameter); 
	}

	//Description: Gets hitbox
	//Parameters: none
	//return: rectangle hitbox
	public Rectangle getBounds() {
		return new Rectangle(x - 10, y, width + 10, height);
	}

	//Description: Check collision with player
	//Parameters: hitbox
	//return: true or false
	public boolean checkCollision(Rectangle playerBounds) {
		return getBounds().intersects(playerBounds);
	}

	//Description: Checks if out of bounds
	//Parameters: screen parameters
	//return: true or false
	public boolean isOutOfBounds(int screenWidth, int screenHeight) {

		return x + width < 0; // Left edge of screen

	}

	//Getter
	public int getSpeed() {
		return speed;
	}


}
