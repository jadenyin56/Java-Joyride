import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

@SuppressWarnings("serial") //funky warning, just suppress it. It's not gonna do anything. (Okay!)
public class JetpackJoyride extends JPanel implements Runnable, ActionListener, MouseListener, KeyListener {

	//Screen Variables
	int FPS = 60;
	Thread thread;
	int width = 1000;
	int height = 600;

	//Screen stuff
	int screenX = 0;
	int speed = 1;

	//Scoring System
	int score = 0;
	int scoringIncrement = 1;

	// Main Game Background Variables
	BufferedImage[] backgroundImages = new BufferedImage[10];
	BufferedImage transitionImage;
	int backgroundIndex1 = 0;
	int backgroundIndex2 = 0;
	int backgroundX1 = 0;

	//Main game update variables
	// Spawn every 5000ms
	int screenUpdateInterval = 4000; 
	long lastScreenUpdateTime;

	//Player Variable
	Player player;
	// Key state
	boolean activateJetpack = false;

	// Coin-related variables
	ArrayList<Coin> coins = new ArrayList<>();
	int coinCount = 0; // Total coins collected
	long lastCoinSpawnTime = System.currentTimeMillis();
	int nextCoinSpawnInterval = 5000; 
	private int coinCounter = 0;

	//Fireball Variables
	ArrayList<FireballObstacle> fireballs = new ArrayList<>();
	int fireballSpawnInterval = 3000; // Spawn every 1000ms
	long lastFireballSpawnTime = System.currentTimeMillis();

	//Missile Variables
	ArrayList<MissileObstacle> missiles = new ArrayList<>();
	MissilePattern missilePattern;
	boolean showWarning = false; // Whether to display the warning
	int warningX = 0, warningY = 0; // Warning position
	int warningTargetY = 0;    // Final Y position for the missile
	long warningStartTime = 0; // Timestamp for when the warning starts
	int warningDuration = 2000; // Duration (in ms) to display the warning
	int missileSpeed = 10;
	int counter = 0;

	//Missile Warning Variables
	BufferedImage[] missileWarningImages = new BufferedImage[2];
	int currentMissileWarningImage = 0;

	//Laser Variables
	// Flag to indicate whether lasers are active
	ArrayList<LaserObstacle> lasers = new ArrayList<>();
	boolean lasersActive = false;
	boolean lasersDrawn = false;
	boolean laserWarningPeriod = false;
	// Spawn every 5000ms
	int laserSpawnInterval = 30000; 
	long lastLaserSpawnTime = System.currentTimeMillis();
	int laserLastingInterval = 1500;
	int laserWarningInterval = 1000;

	// Constructor
	public JetpackJoyride() {
		// Initialize the JPanel
		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		addKeyListener(this);
		setFocusable (true);
		//so you can actually get mouse input
		addMouseListener(this);
		//self explanatory. You want to see your frame
		//Starting the thread
		thread = new Thread(this);
		thread.start();
	}

	// Paint the current screen
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		// Draw current and next background for scrolling
		g.drawImage(backgroundImages[backgroundIndex1], backgroundX1, 0, width, height, null);
		g.drawImage(transitionImage, backgroundX1 + width, 0, 60, height, null);
		g.drawImage(backgroundImages[backgroundIndex2], backgroundX1 + width + 60, 0, width, height, null);


		// Draw the score and speed
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 24));
		g.drawString("Score: " + score, 20, 40);

		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("Speed: " + speed, 20, 70);

		// Draw the player
		player.draw(g);


		// Draw coins
		for (Coin coin : coins) {
			coin.draw(g);
		}

		// Draw the coin counter
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Arial", Font.BOLD, 24));
		g.drawString("Coins: " + coinCounter, 20, 100);

		// Draw fireballs
		for (FireballObstacle fireball : fireballs) {
			fireball.draw(g);
		}

		// Missiles
		for (MissileObstacle missile : missiles) {
			missile.draw(g);
		}


		// Lasers
		for (LaserObstacle laser : lasers) {
			laser.draw(g);
		}



		// Missile Warning
		if (showWarning && lasersActive == false && laserWarningPeriod == false) {
			g.drawImage(missileWarningImages[currentMissileWarningImage], warningX + 900, warningY - 50, 70, 70, null);
		}
	}

	public void initialize() {

		// Initialize the player (start position at (200, 500), size 40x40)
		player = new Player("Jaeshaun", 200, 500, 50, 50, 10, 10);

		missilePattern = new MissilePattern(width, height, 2000, 500, 50);

		try {
			for (int i = 0; i < 10; i++) {
				backgroundImages[i] = ImageIO.read(new File("actualBACK" + (i + 1) + ".jpg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		backgroundIndex1 = (int)(Math.random()*(9));
		// Ensure no immediate repetition
		backgroundIndex2 = (backgroundIndex1 + 1) % 10; 

		try {
			transitionImage = ImageIO.read(new File("transition.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
			missileWarningImages[0] = ImageIO.read(new File("redMissile.png"));
			missileWarningImages[1] = ImageIO.read(new File("yellowMissile.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void spawnCoinPattern() {

		if (laserWarningPeriod) {
			return;
		}

		// Spawn off Screen
		int coinX = 1100;
		// Random Y between 100 and 500
		int coinY = (int) (Math.random()*(400)) + 100;
		String[] patterns = {"line", "triangle", "cluster", "wave", "zigzag", "circle", "spiral"};
		String pattern = patterns[((int)(Math.random()*(patterns.length)))];

		// Create a new coin pattern and add coins to the list
		System.out.println(coinY);
		System.out.println(pattern);
		CoinPattern coinPattern = new CoinPattern(coinX, coinY, pattern);

		coins.addAll(coinPattern.getCoins());
	}

	//Spawm Fireball
	public void spawnFireball() {

		if (laserWarningPeriod) {
			return;
		}

		int horzOrVert = (int)(Math.random()*(2));

		// Standard Width/Height
		int fireBallwidth; 
		int fireBallheight;

		// Random vertical position
		int y = (int)(Math.random()*(420)) + 90; 
		// Spawn slightly off of screen to the right
		int x = width; 

		if (horzOrVert == 0) {
			// Random width between 60 and 260
			fireBallwidth = (int)(Math.random()*(200)) + 60;
			fireBallheight = 40;

			fireballs.add(new FireballObstacle(x + 1000, y, fireBallwidth, fireBallheight, speed));
		}

		else {
			// Same but with the height
			fireBallheight = (int)(Math.random()*(200)) + 60; 
			fireBallwidth = 40;

			fireballs.add(new FireballObstacle(x + 1000, y, fireBallwidth, fireBallheight, speed));
		}


	}



	// Generate random laser positions (either top, bottom, or middle)
	public  void spawnLasers(int screenWidth, int screenHeight, int laserSpeed) {

		int laserCount = (int)(Math.random()*(4)) + 1; // Generates between 1 and 4 lasers

		for (int i = 0; i < laserCount; i++) {

			int laserX = 0; 

			// Randomly choose top, middle, or bottom for Y position
			int laserY;
			int positionChoice = (int)(Math.random()*(3));
			
			if (positionChoice == 0) { // Top
				laserY = 80;
			} 
			else if (positionChoice == 1) { // Middle
				laserY = screenHeight / 2 - 10 / 2;
			} 
			else { // Bottom
				laserY = screenHeight - 80;

			}

			if (speed >= 15) {
				lasers.add(new LaserObstacle(laserX, laserY, speed, width, (int)(Math.random()*(3))-1));
			}
			else {
				lasers.add(new LaserObstacle(laserX, laserY, speed, width, 0));
			}
		}
	}


	@Override
	public void run() {

		initialize();

		while(true) {

			updateMainGame();
			updateScreen();
			updatePlayer();
			updateCoins();
			updateFireballs();
			updateMissiles();
			updateLasers();

			this.repaint();

			try {
				Thread.sleep(1000 / 60); // 60 FPS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void updateMainGame() {
		// Move the background to the left
		backgroundX1 -= speed;

		if(backgroundX1 + 60 < -width) {
			backgroundX1 = 0;

			// Randomly select the next background (different from the current one)
			int newBackgroundIndex = 0;

			do {
				newBackgroundIndex = (int)(Math.random()*(9));
			} 

			while (newBackgroundIndex == backgroundIndex1);

			backgroundIndex1 = backgroundIndex2;
			backgroundIndex2 = newBackgroundIndex;
		}


	}

	public void updateScreen() {

		long lastScoreUpdate = System.currentTimeMillis(); // Track the last time the score was updated
		int scoreUpdateInterval = 100; // Update score every second (1000 ms)
		// Update score at regular intervals
		if (System.currentTimeMillis() - lastScoreUpdate >= scoreUpdateInterval) {
			score += scoringIncrement; // Increment score
			lastScoreUpdate = System.currentTimeMillis();
		}
		
		System.out.println(System.currentTimeMillis());

		//Speeds up the screen every 5 seconds
		if (System.currentTimeMillis() - lastScreenUpdateTime >= screenUpdateInterval) {
			System.out.println("Updated");
			speed += 1;
			fireballSpawnInterval -= 10;
			missileSpeed += 1;
			lastScreenUpdateTime = System.currentTimeMillis();
			screenUpdateInterval += 500;
		}
	}

	public void updatePlayer() {
		// Update player

		if (!(player.getFuel() > 0)) {
			player.move(false);
		}

		else {
			player.move(activateJetpack);
		}


		// Refuel the jetpack if grounded
		// Assuming ground level is at y = 530
		if (player.getBounds().y == 530) { 
			player.refuel();
		}
	}

	public void updateCoins() {
		// Move coins and check collisions
		for (int i = 0; i < coins.size(); i++) {
			Coin coin = coins.get(i);
			coin.move(speed);

			// Remove coin if it moves off-screen
			if (coin.getBounds().x + 20 < 0) {
				coins.remove(i);
				i--;
			} 

			//remove Coin if intersects with jit

			for (int k = fireballs.size() - 1; k >= 0; k--) {
				FireballObstacle fireball = fireballs.get(k);

				// Check collision with a fireball obstacle
				if (coin.getBounds().intersects(fireball.getBounds())) {
					coins.remove(i);
				}

			}

			if (player.getBounds().intersects(coin.getBounds())) {
				// Player collects the coin
				coinCounter += coin.getValue();
				coins.remove(i);
				i--;
			}



			if (speed >= 3) {
				nextCoinSpawnInterval = 2000;
			}
		}

		// Spawn new coins periodically
		if (System.currentTimeMillis() - lastCoinSpawnTime >= nextCoinSpawnInterval) {
			spawnCoinPattern();
			lastCoinSpawnTime = System.currentTimeMillis();
		}
	}

	public void updateFireballs() {

		for (int i = fireballs.size() - 1; i >= 0; i--) {
			FireballObstacle fireball = fireballs.get(i);
			fireball.update(speed);

			// Check collision with the player
			if (fireball.checkCollision(player.getBounds())) {
				System.out.println("Collision detected!");
				// Handle collision logic (e.g., end game, reduce health)
			}

		}

		// Spawn new fireballs periodically
		if (System.currentTimeMillis() - lastFireballSpawnTime >= fireballSpawnInterval) {
			spawnFireball();
			if (fireballSpawnInterval >= 1000) {
				fireballSpawnInterval -= 50;
			}
			lastFireballSpawnTime = System.currentTimeMillis();
		}


	}


	public void updateMissiles() {

		long currentTime = System.currentTimeMillis();

		if (showWarning) {

			counter += 1;
			// Update the warning to follow the player's Y position
			warningY = player.getBounds().y + player.getBounds().height / 2;

			if (currentMissileWarningImage == 0 && counter >= 10) {
				currentMissileWarningImage = 1;
				if (counter >= 20){
					counter = 0;
				}
			}

			else {
				currentMissileWarningImage = 0;
			}

			// Check if the warning duration has elapsed
			if (currentTime - warningStartTime >= warningDuration) {
				showWarning = false; // Stop showing the warning
				warningTargetY = warningY; // Set the final position for the missile

				int missileX = width; // Missile spawns off-screen to the right
				int missileWidth = 40;
				int missileHeight = 20;

				if (!laserWarningPeriod) {
					missiles.add(new MissileObstacle(missileX, warningTargetY - missileHeight / 2, missileWidth, missileHeight, missileSpeed));
				}
			}
		} 

		else {
			// Randomly decide to spawn a new warning
			if (Math.random() < 0.01) { // ~1% chance per frame
				showWarning = true;
				warningStartTime = currentTime; // Start the warning timer
			}
		}

		// Update existing missiles
		for (int i = missiles.size() - 1; i >= 0; i--) {
			MissileObstacle missile = missiles.get(i);
			missile.move();

			if (lasersActive) {
				missiles.remove(i);
			}

			// Check collision with the player
			if (missile.checkCollision(player.getBounds())) {
				System.out.println("Missile hit the player!");
				// Handle collision logic (e.g., end game, reduce health)
			}

			// Remove missiles that are out of bounds
			if (missile.isOutOfBounds(width)) {
				missiles.remove(i);
			}
		}
	}

	public void updateLasers() {

		// Spawn new lasers periodically
		if (System.currentTimeMillis() - lastLaserSpawnTime >= laserSpawnInterval) {
			//Warning Period
			laserWarningPeriod = true; 
		}

		if (isScreenClear()) {
			lasersActive = true;
		}

		if (System.currentTimeMillis() - lastLaserSpawnTime >= laserSpawnInterval && lasersActive) {
			lasersDrawn = true;
		}
		// Spawn new lasers periodically
		if (System.currentTimeMillis() - lastLaserSpawnTime >= laserSpawnInterval + laserWarningInterval && lasersDrawn) {
			spawnLasers(width,height,speed);
			lastLaserSpawnTime = System.currentTimeMillis();
		}


		if (lasersActive) {
			for (int i = lasers.size() - 1; i >= 0; i--) {
				LaserObstacle laser = lasers.get(i);
				laser.move();

				// Check collision with the player
				if (laser.checkCollision(player.getBounds())) {
					System.out.println("Collision detected!");
					// Handle collision logic (e.g., end game, reduce health)
				}

			}

			if (System.currentTimeMillis() - lastLaserSpawnTime >= 2*laserLastingInterval) {
				lasersActive = false;
				laserWarningPeriod = false;
			}
		}

		else {
			for (int i = lasers.size() - 1; i >= 0; i--) {
				lasers.remove(i);
			}
		}

	}

	public boolean isScreenClear() {
		// Check coins
		for (Coin coin : coins) {
			if (coin.getBounds().x + coin.getBounds().width > 0) {
				return false; // A coin is still on the screen
			}
		}

		// Check fireballs
		for (FireballObstacle fireball : fireballs) {
			if (fireball.getBounds().x + fireball.getBounds().width > 0) {
				return false; // A fireball is still on the screen
			}
		}

		// Check missiles
		for (MissileObstacle missile : missiles) {
			if (missile.getBounds().x + missile.getBounds().width > 0) {
				return false; // A missile is still on the screen
			}
		}

		// Check lasers
		for (LaserObstacle laser : lasers) {
			if (laser.getBounds().x + laser.getBounds().width > 0) {
				return false; // A laser is still on the screen
			}
		}

		// If no obstacles are on-screen
		return true;
	}


	// Main method
	public static void main(String[] args) {
		//The following lines create your window

		//makes a brand new JFrame
		JFrame frame = new JFrame ("Jetpack Joyride");
		//makes a new copy of your "game" that is also a JPanel
		JetpackJoyride myPanel = new JetpackJoyride ();
		//so your JPanel to the frame so you can actually see it
		frame.add(myPanel);
		//so you can actually get keyboard input
		frame.setVisible(true);
		//some weird method that you must run
		frame.pack();
		//place your frame in the middle of the screen
		frame.setLocationRelativeTo(null);
		//without this, your thread will keep running even when you windows is closed!
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//self explanatory. You don't want to resize your window because
		//it might mess up your graphics and collisions
		frame.setResizable(false);
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Activate jetpack when the mouse is pressed
		activateJetpack = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Deactivate jetpack when the mouse is released
		activateJetpack = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W) {
			activateJetpack = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W) {
			activateJetpack = false;
		}
	}
}