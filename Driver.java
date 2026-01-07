/**
 Jaden Yin
 Final EVER CULMINATING (Thank God)
 Game: Jetpack Joyride
 Main Driver class
 **/

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

@SuppressWarnings("serial") //funky warning, just suppress it. It's not gonna do anything. (Okay!)
public class Driver extends JPanel implements Runnable, ActionListener, MouseListener, KeyListener {

	//makes a brand new JFrame
	static JFrame frame;
	
	//Gamestates
	int gamestate = -1;

	//Screen Variables
	int FPS = 60;
	Thread thread;
	int width = 1000;
	int height = 600;

	Clip background, winnerSound;

	//LogoScreen Variables
	BufferedImage logoImage;

	// Loading screen variables (Can change later)
	BufferedImage loadingImage;
	int loadingProgress = 0;

	// Shop and Skins Image
	BufferedImage comingSoonImage;

	// Main Menu Screen Variables
	BufferedImage mainMenuImage;
	//Player Name Input
	private String playerName = "";

	// Main Game Variables
	//Screen stuff
	int screenX = 0;
	int speed = 1;

	//Scoring System
	int highScore = 0;
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
	boolean isAlive = false;

	// Coin-related variables
	ArrayList<Coin> coins = new ArrayList<>();
	// Total coins collected
	int coinCount = 0; 
	long lastCoinSpawnTime = System.currentTimeMillis();
	int nextCoinSpawnInterval = 5000; 
	private int coinCounter = 0;

	//Fireball Variables
	ArrayList<FireballObstacle> fireballs = new ArrayList<>();
	int fireballSpawnInterval = 3000;
	long lastFireballSpawnTime = System.currentTimeMillis();

	//Missile Variables
	ArrayList<MissileObstacle> missiles = new ArrayList<>();
	MissilePattern missilePattern;
	boolean showWarning = false; 
	int warningX = 0, warningY = 0; 
	int warningTargetY = 0;   
	long warningStartTime = 0; 
	int warningDuration = 2000; 
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

	//Instructions Variables
	BufferedImage instructionsImage;

	//Highscores Variables
	BufferedImage highscoresImage;

	//Credits Variables
	BufferedImage creditsImage;

	//Buycoins Variables
	BufferedImage buyCoinsImage;

	//Pause Screen Variables
	BufferedImage pauseScreen;

	//Death Screen Variables
	BufferedImage deathScreenImage;
	int deathSpeed;
	int deathCoins;
	int deathScore;

	//Description: Constructor
	//Parameters: none
	//return: none
	public Driver() {

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

		//LOGO STUFF
		//Load the logo image
		try {
			logoImage = ImageIO.read(new File("actualLOGO.jpg"));
		} catch (IOException e) {
			System.out.println("Logo image not found.");
			e.printStackTrace();
		}

		//LOADING STUFF
		//Load the loading image
		try {
			loadingImage = ImageIO.read(new File("actualLOADING.jpg"));
		} catch (IOException e) {
			System.out.println("Loading image not found.");
			e.printStackTrace();
		}

		//SHOP AND SKINS
		//Loading the coming soon image
		try {
			comingSoonImage = ImageIO.read(new File("comingSoon.jpg"));
		} catch (IOException e) {
			System.out.println("comingSoon image not found.");
			e.printStackTrace();
		}

		//MENU STUFF
		//Load the menu image
		try {
			mainMenuImage = ImageIO.read(new File("actualMENU.jpg"));
		} catch (IOException e) {
			System.out.println("Main Menu image not found.");
			e.printStackTrace();
		}

		//INSTRUCTIONS STUFF
		//Loading the instructions image
		try {
			instructionsImage = ImageIO.read(new File("instructions.jpg"));
		} catch (IOException e) {
			System.out.println("Instructions image not found.");
			e.printStackTrace();
		}


		//HIGHSCORES STUFF
		//Loading the highscores image
		try {
			highscoresImage = ImageIO.read(new File("highscores.jpg"));
		} catch (IOException e) {
			System.out.println("Highscores image not found.");
			e.printStackTrace();
		}


		//CREDITS STUFF
		//Loading the credits image
		try {
			creditsImage = ImageIO.read(new File("credits.jpg"));
		} catch (IOException e) {
			System.out.println("Credits image not found.");
			e.printStackTrace();
		}

		//BUYCOINS STUFF
		//Loading the buyCoins image
		try {
			buyCoinsImage = ImageIO.read(new File("buyCoins.jpg"));
		} catch (IOException e) {
			System.out.println("buyCoins image not found.");
			e.printStackTrace();
		}

		//Death Screen STUFF
		//Loading the Death screen image
		try {
			deathScreenImage = ImageIO.read(new File("actualDeath.jpg"));
		} catch (IOException e) {
			System.out.println("death image not found.");
			e.printStackTrace();
		}

		//Adding the music
		try {

			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("background.wav"));
			background = AudioSystem.getClip();
			background.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("winner.wav"));
			winnerSound = AudioSystem.getClip();
			winnerSound.open(sound);
		}

		catch (Exception e) {
		}

		//Running Background music
		background.start();
		background.setFramePosition (0); 
		background.loop(Clip.LOOP_CONTINUOUSLY);

		// To set "goodbye" to play when window closes
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing (java.awt.event.WindowEvent windowEvent) {
				winnerSound.start();
				winnerSound.setFramePosition(0);
				background.stop();
				try
				{
					Thread.sleep (1200);
				}
				catch (InterruptedException e)
				{
				}
				System.exit(0);
			}   
		});	

		// Initialize the player (start position at (200, 500), size 40x40)
		player = new Player(playerName, 200, 500, 50, 50, highScore, coinCount);

	}

	// Paint the current screen (paintcomponent)
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//Logo Screen
		if (gamestate == -1) {
			drawLogoScreen(g);
		}

		//Loading Screen
		if (gamestate == 0) {
			drawLoadingScreen(g);
		}

		//Main Menu Screen
		if (gamestate == 1) {
			drawMenuScreen(g);
			// Draw the coin counter
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString("" + coinCount, 800, 100);
		}

		//Actual Game Screen
		if (gamestate == 2) {

			// Draw current and next background for scrolling
			g.drawImage(backgroundImages[backgroundIndex1], backgroundX1, 0, width, height, null);
			g.drawImage(transitionImage, backgroundX1 + width, 0, 60, height, null);
			g.drawImage(backgroundImages[backgroundIndex2], backgroundX1 + width + 60, 0, width, height, null);

			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.setColor(Color.WHITE);
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

		//Shop Screen (Tmr)
		if (gamestate == 3) {
			drawComingSoonScreen(g);
		}

		//Skins Screen (Tmr)
		if (gamestate == 4) {
			drawComingSoonScreen(g);
		}

		//Instructions Screen
		if (gamestate == 5) {
			drawInstructionsScreen(g);
		}

		//Highscores Screen
		if (gamestate == 6) {
			drawHighscoresScreen(g);
		}

		//Credits Screen
		if (gamestate == 7) {
			drawCreditsScreen(g);
		}

		//BuyCoins Screen
		if (gamestate == 8) {
			drawBuyCoinsScreen(g);
		}

		//Pause Screen
		if (gamestate == 9) {

		}

		//Death screen
		if (gamestate == 10) {
			drawDeathScreen(g);
		}

	}

	//Description: Initializes game components
	//Parameters: None
	//return: none
	public void initialize() {

		player.setPlayerName(playerName);
		player.setCurrentScore(0);
		player.setStartY(500);

		//Initializes missile pattern
		missilePattern = new MissilePattern(width, height, 2000, 500, 50);

		//Load Images
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

		//Load more images
		try {
			transitionImage = ImageIO.read(new File("transition.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}


		//EVEN MORE
		try {
			missileWarningImages[0] = ImageIO.read(new File("redMissile.png"));
			missileWarningImages[1] = ImageIO.read(new File("yellowMissile.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//SPAWNING METHODS
	//IMPORTANT SECTION

	//Description: Spawns in the coins using the coinPattern class
	//Parameters: none
	//return: none
	public void spawnCoinPattern() {

		//if no lasers on screen
		if (laserWarningPeriod) {
			return;
		}

		// Spawn off Screen
		int coinX = 1100;
		// Random Y between 100 and 500
		int coinY = (int) (Math.random()*(400)) + 100;
		String[] patterns = {"line", "triangle", "cluster", "wave", "zigzag", "circle", "spiral"};
		String pattern = patterns[((int)(Math.random()*(patterns.length)))];

		//Make new object
		CoinPattern coinPattern = new CoinPattern(coinX, coinY, pattern);

		//add to arraylist
		coins.addAll(coinPattern.getCoins());
	}

	//Description: Spawns in the fireballs 
	//Parameters: none
	//return: none
	public void spawnFireball() {

		//If no lasers
		if (laserWarningPeriod) {
			return;
		}

		//Sees if it is horizontal or vertical
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


	//Description: Spawns in the lasers 
	//Parameters: Screenwidth, screenheight, laserSpeed (all of them are useless I realize)
	//return: none
	public void spawnLasers() {

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
				laserY = height / 2 - 10 / 2;
			} 
			else { // Bottom
				laserY = height - 80;

			}

			//If speed is high, up the difficulty!
			if (speed >= 15) {
				lasers.add(new LaserObstacle(laserX, laserY, speed, width, (int)(Math.random()*(3))-1));
			}
			//Otherwise, static lasers
			else {
				lasers.add(new LaserObstacle(laserX, laserY, speed, width, 0));
			}
		}
	}


	//RUN METHOD
	//Description: run method
	//Parameters: none
	//return: none
	@Override
	public void run() {

		while(true) {

			//Show logo screen for like 2 seconds
			if (gamestate == -1) {
				try {
					//change back later
					Thread.sleep(2000);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				} 
				//Transition to loading screen
				gamestate = 0; 
				this.repaint();
			}

			//Loading Screen (Runs loading screen method)
			if (gamestate == 0) {

				while (loadingProgress <= 275) {
					updateLoadingScreen();
					this.repaint(); 
					try {
						Thread.sleep(1000 / 60); // 60 FPS
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				gamestate = 1;
				this.repaint();
			}

			//Main Menu
			if (gamestate == 1) {

				this.repaint();
			}

			//Main Game
			if (gamestate == 2) {

				isAlive = true;
				initialize();
				System.out.println("hello");

				while (isAlive) {

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


			//If shop screen (tmr)
			if (gamestate == 3) {
				this.repaint();
			}

			//If skins screen (tmr)
			if (gamestate == 4) {
				this.repaint();
			}

			//If instructions screen, just repaint
			if (gamestate == 5) {
				this.repaint();
			}

			//If highscores screen, just repaint
			if (gamestate == 6) {
				this.repaint();
			}

			//If credits screen, just repaint
			if (gamestate == 7) {
				this.repaint();
			}

			//If buyCoins screen, just repaint
			if (gamestate == 8) {
				this.repaint();
			}

			//If buyCoins screen, just repaint
			if (gamestate == 9) {
				this.repaint();
			}

			//If buyCoins screen, just repaint
			if (gamestate == 10) {				

				this.repaint();
			}

			this.repaint();

			try {
				Thread.sleep(1000 / 60); // 60 FPS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	//SUPER IMPORTANT
	//Description: Resets all variables 
	//Parameters: none
	//return: none
	public void resetGame() {
		//Reset Variables
		deathSpeed = speed;
		deathCoins = coinCounter;
		deathScore = player.getCurrentScore();

		speed = 1;
		fireballSpawnInterval = 3000;
		activateJetpack = false;
		missileSpeed = 10;
		screenUpdateInterval = 4000;
		isAlive = false;

		lasersActive = false;
		lasersDrawn = false;
		laserWarningPeriod = false;

		showWarning = false;
		coinCount += coinCounter;
		coinCounter = 0;

		// Remove coins
		for (int i = 0; i < coins.size(); i++) {
			coins.remove(i);
		}

		// Move coins and check collisions
		for (int i = 0; i < fireballs.size(); i++) {
			fireballs.remove(i);
		}

		// Move coins and check collisions
		for (int i = 0; i < missiles.size(); i++) {
			missiles.remove(i);
		}

		// Move coins and check collisions
		for (int i = 0; i < lasers.size(); i++) {
			lasers.remove(i);
		}

		//If new highScore
		if (score >= highScore) {
			highScore = score;
		}

		score = 0;
	}

	//Description: Updates the loading bar
	//Parameters: None
	//Returns: None
	public void updateLoadingScreen() {
		loadingProgress += 1;	
	}


	//Description: Draws the menu screen with loading bar
	//Parameters: Graphics g
	//Returns: Void
	public void drawMenuScreen(Graphics g) {

		g.drawImage(mainMenuImage, 0, 0, null);


	}

	//Description: Updates the main game backgrounds
	//Parameters: None
	//Returns: None
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

	//Description: Updates the main game screen stuff (scores, speed)
	//Parameters: None
	//Returns: None
	public void updateScreen() {

		long lastScoreUpdate = System.currentTimeMillis(); 
		// Track the last time the score was updated
		int scoreUpdateInterval = 100; 
		// Update score every second (1000 ms)
		// Update score at regular intervals
		if (System.currentTimeMillis() - lastScoreUpdate >= scoreUpdateInterval) {
			score += scoringIncrement; // Increment score
			lastScoreUpdate = System.currentTimeMillis();
		}

		//Speeds up the screen every 5 seconds
		if (System.currentTimeMillis() - lastScreenUpdateTime >= screenUpdateInterval) {

			//Change other variables too lol
			speed += 1;
			fireballSpawnInterval -= 10;
			missileSpeed += 1;
			lastScreenUpdateTime = System.currentTimeMillis();
			screenUpdateInterval += 500;


		}
	}

	//Description: Updates the player based on user input
	//Parameters: None
	//Returns: None
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

	//Description: Updates the coin 
	//Parameters: None
	//Returns: None
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

			//Player collecting coin
			if (player.getBounds().intersects(coin.getBounds())) {
				// Player collects the coin
				coinCounter += coin.getValue();
				coins.remove(i);
				i--;
			}

			//Spawn coins faster as game progresses
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

	//Description: Updates the fireballs
	//Parameters: None
	//Returns: None
	public void updateFireballs() {

		//Updates all of them
		for (int i = 0; i < fireballs.size(); i++) {
			FireballObstacle fireball = fireballs.get(i);
			fireball.update(speed);

			// Check collision with the player
			if (fireball.checkCollision(player.getBounds())) {
				fireballs.remove(i);
				isAlive = false;
				resetGame();
				gamestate = 10;
				winnerSound.start();
				winnerSound.setFramePosition(0);
				break;

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

	//Description: Updates the missiles 
	//Parameters: None
	//Returns: None
	public void updateMissiles() {

		long currentTime = System.currentTimeMillis();

		//If warning period
		if (showWarning) {

			counter += 1;
			// Update the warning to follow the player's Y position
			warningY = player.getBounds().y + player.getBounds().height / 2;

			//Frames for warning
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
				missiles.remove(i);
				isAlive = false;
				resetGame();
				gamestate = 10;
				winnerSound.start();
				winnerSound.setFramePosition(0);
				break;
				// Handle collision logic (e.g., end game, reduce health)
			}

			// Remove missiles that are out of bounds
			if (missile.isOutOfBounds(width)) {
				missiles.remove(i);
			}
		}
	}

	//Description: Updates the lasers 
	//Parameters: None
	//Returns: None
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
			spawnLasers();
			lastLaserSpawnTime = System.currentTimeMillis();
		}


		//If lasers are active
		if (lasersActive) {
			for (int i = lasers.size() - 1; i >= 0; i--) {
				LaserObstacle laser = lasers.get(i);
				laser.move();

				// Check collision with the player
				if (laser.checkCollision(player.getBounds())) {
					lasers.remove(i);
					isAlive = false;
					resetGame();
					gamestate = 10;
					winnerSound.start();
					winnerSound.setFramePosition(0);
					break;
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

	//Description: Checks if screen has objects on it for lasers
	//Parameters: none
	//Returns: b
	public boolean isScreenClear() {
		// Check coins
		for (Coin coin : coins) {
			if (coin.getBounds().x + coin.getBounds().width > 0) {
				// A coin is still on the screen
				return false; 
			}
		}

		// Check fireballs
		for (FireballObstacle fireball : fireballs) {
			if (fireball.getBounds().x + fireball.getBounds().width > 0) {
				// A fireball is still on the screen
				return false; 
			}
		}

		// Check missiles
		for (MissileObstacle missile : missiles) {
			if (missile.getBounds().x + missile.getBounds().width > 0) {
				// A missile is still on the screen
				return false; 
			}
		}

		// Check lasers
		for (LaserObstacle laser : lasers) {
			if (laser.getBounds().x + laser.getBounds().width > 0) {
				// A laser is still on the screen
				return false; 
			}
		}

		// If no obstacles are on-screen
		return true;
	}

	//Description: Draws the logo at the start
	//Parameters: Graphics g
	//Returns: Void
	public void drawLogoScreen(Graphics g) {
		g.drawImage(logoImage, 150, 50, null);

	}

	//Description: Draws the loading screen with loading bar
	//Parameters: Graphics g
	//Returns: Void
	public void drawLoadingScreen(Graphics g) {

		g.drawImage(loadingImage, 0, 0, null);
		// Simulated loading bar
		g.setColor(Color.WHITE);
		g.fillRect(333, 580, loadingProgress, 15);
		g.setColor(Color.BLACK);
		g.drawRect(333, 580, 275, 15); 

	}

	//Description: Draws the comingSoon screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawComingSoonScreen(Graphics g) {

		g.drawImage(comingSoonImage, 0, 0, null);

	}

	//Description: Draws the instructions screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawInstructionsScreen(Graphics g) {

		g.drawImage(instructionsImage, 0, 0, null);

	}

	//Description: Draws the highscores screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawHighscoresScreen(Graphics g) {

		g.drawImage(highscoresImage, 0, 0, null);

	}

	//Description: Draws the credits screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawCreditsScreen(Graphics g) {

		g.drawImage(creditsImage, 0, 0, null);

	}

	//Description: Draws the buyCoins screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawBuyCoinsScreen(Graphics g) {

		g.drawImage(buyCoinsImage, 0, 0, null);

	}

	//Description: Draws the death screen
	//Parameters: Graphics g
	//Returns: Void
	public void drawDeathScreen(Graphics g) {

		g.drawImage(deathScreenImage, 0, 0, null);
		// Display score and high score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 36));
		g.drawString("Name: " + playerName, 300, 250);
		g.drawString("Score: " + deathScore, 300, 300);
		g.drawString("Coins Collected: " + deathCoins, 300, 350);
		g.drawString("Speed Reached: " + deathSpeed, 300, 400);

	}




	// Main method
	public static void main(String[] args) {
		//The following lines create your window

		//makes a brand new JFrame
		frame = new JFrame ("Main Game Driver");
		//makes a new copy of your "game" that is also a JPanel
		Driver myPanel = new Driver ();
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

		//Get the x and y coordinates of click
		int mouseX = e.getX();
		int mouseY = e.getY();

		//System.out.println(mouseX + "," +mouseY);		
		//If it is the main menu
		if (gamestate == 1) {

			//Make rectangles for the main game
			Rectangle mainRectangle1 = new Rectangle(240, 60, 490, 480); 
			Rectangle mainRectangle2 = new Rectangle(730, 130, 510, 410); 


			//Make rectangles for each of the buttons
			//Name Rectangle (Not a gamestate just a jTextBox)
			Rectangle nameRectangle = new Rectangle(58, 155, 165, 50); 
			//Shop Rectangle (gamestate = 3)
			Rectangle shopRectangle = new Rectangle(58, 217, 165, 55); 
			//Skin Rectangle (gamestate = 4)
			Rectangle skinRectangle = new Rectangle(58, 279, 165, 50); 
			//Instruction Rectangle (gamestate = 5)
			Rectangle instructionRectangle = new Rectangle(58, 343, 165, 50); 
			//Highscores Rectangle (gamestate = 6)
			Rectangle highscoreRectangle = new Rectangle(58, 406, 165, 50); 
			//Credits Rectangle (gamestate = 7)
			Rectangle creditRectangle = new Rectangle(58, 471, 165, 50); 
			//Coins Rectangle (gamestate = 8)
			Rectangle coinRectangle = new Rectangle(751, 66, 180, 45); 
			//Settings Rectangle (gamestate = 9) - Prolly will not have

			//If name button is clicked
			if (nameRectangle.contains(mouseX, mouseY)) {
				// Show an input dialog screen to get the player's name
				String inputName = JOptionPane.showInputDialog(this, "Enter your name:", "Name Input", JOptionPane.PLAIN_MESSAGE);

				// If the user enters a name, store it as the new player name also no empty names
				if (inputName != null && !inputName.trim().isEmpty()) {
					//Trim the input
					playerName = inputName.trim();		     
				} 

				//Otherwise display thing
				else {
					JOptionPane.showMessageDialog(this, "Cannot have EMPTY NAME", "STOP TRYING TO CRASH MY CODE", JOptionPane.PLAIN_MESSAGE);
				}
			}

			//Main
			//If main menu is clicked
			if (mainRectangle1.contains(mouseX, mouseY) || mainRectangle2.contains(mouseX, mouseY)) {

				if (playerName.equals("")) {
					JOptionPane.showMessageDialog(this, "Cannot have EMPTY NAME", "STOP TRYING TO CRASH MY CODE", JOptionPane.PLAIN_MESSAGE);
				}

				else {
					gamestate = 2;
					repaint();	
				}

			}

			//Shop Rectangle
			//If shop is clicked
			if (shopRectangle.contains(mouseX, mouseY)) {
				gamestate = 3;
				repaint();		        
			}

			//Skin Rectangle
			//If skin is clicked
			if (skinRectangle.contains(mouseX, mouseY)) {
				gamestate = 4; 
				repaint();
			}

			//Instruction Rectangle
			//If instruction is clicked
			if (instructionRectangle.contains(mouseX, mouseY)) {		        
				gamestate = 5; 
				repaint();
			}

			//Highscore Rectangle
			//If highscore is clicked
			if (highscoreRectangle.contains(mouseX, mouseY)) {	        
				gamestate = 6; 
				repaint();
			}

			//Credits Rectangle
			//If credits is clicked
			if (creditRectangle.contains(mouseX, mouseY)) {	     
				gamestate = 7; 
				repaint();
			}

			//Coin Rectangle
			//If coins is clicked
			if (coinRectangle.contains(mouseX, mouseY)) {   
				gamestate = 8; 
				repaint();
			}
		}

		//In these gamestates, if clicked in the X box (cuz used same image), return to menu
		if (gamestate == 3 || gamestate == 4 || gamestate == 5 || gamestate == 6 || gamestate == 7 || gamestate == 8 || gamestate == 10) {

			//Get the rectangle
			Rectangle xButtonRectangle = new Rectangle(700,50,50,45);

			//If button is clicked, return to menu
			if (xButtonRectangle.contains(mouseX, mouseY)) {   
				gamestate = 1; 
				repaint();
			}

			repaint();
		}

		if (gamestate == 8) {

			Rectangle coin1000 = new Rectangle(311, 183, 174, 176);
			Rectangle coin2000 = new Rectangle(549, 181, 179, 181);

			if (coin1000.contains(mouseX, mouseY)) {
				coinCount += 1000;
			}

			if (coin2000.contains(mouseX, mouseY)) {
				coinCount += 2000;
			}



		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (gamestate == 2) {
			// Activate jetpack when the mouse is pressed
			activateJetpack = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (gamestate == 2) {
			// Activate jetpack when the mouse is pressed
			activateJetpack = false;
		}

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

		if (gamestate == 2) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_W) {
				activateJetpack = true;
			}
		}

		//In these gamestates
		if (gamestate == 3 || gamestate == 4 || gamestate == 5 || gamestate == 6 || gamestate == 7 || gamestate == 8 ||gamestate == 9 || gamestate == 10) {

			//If escape is clicked, return to menu
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ESCAPE) {
				gamestate = 1; 
				repaint();
			}

			repaint();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (gamestate == 2) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_W) {
				activateJetpack = false;
			}
		}

	}
}