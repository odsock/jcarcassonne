package jCarcassonneGUI;

import jCarcassonne.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JFrame;

public class JCarcassonne extends JFrame implements Runnable
{
	private static final long serialVersionUID = 1L;
	//JPanel dimensions
	private int screenWidth;
	private int screenHeight;

	//tile image dimensions
	private final int tileWidth = 128;
	private final int tileHeight = 128;

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;

	//double buffering stuff
	private Graphics2D dbg;
	private Image dbImage = null;

	//landscape scrolling stuff
	private int transX = 0;
	private int transY = 0;
	private boolean northScrollFlag = false;
	private boolean southScrollFlag = false;
	private boolean eastScrollFlag = false;
	private boolean westScrollFlag = false;

	//GUI elements
	private Rectangle peekRectangle = new Rectangle(tileWidth,tileHeight);
	private Rectangle doneButtonRectangle;
	private Rectangle endGameButtonRectangle;
	private boolean showScores = false;
	
	//start screen elements
	private String newPlayerName = new String(); //holds name of new player being added
	private ArrayList<Color> colorList = new ArrayList<Color>();
	private Iterator<Color> colorIterator;

	//interface to game model
	private GameController gameController;

	public static void main(String[] args)
	{
		new JCarcassonne();
	}
	
	public JCarcassonne()
	{
		super("JCarcassonne");
		
		//setup frame dimensions
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(screenDim);
		this.screenWidth = screenDim.width;
		this.screenHeight = screenDim.height;

		//configure frame details
		setBackground(Color.white);
		setFocusable(true);
		requestFocus();    // the JPanel now has focus, so receives key events
	    setUndecorated(true);
	    setIgnoreRepaint(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    pack();
	    setVisible(true);
		
		//GUI buttons
		doneButtonRectangle = new Rectangle(screenWidth-tileWidth/2+10, screenHeight-tileHeight*2-20, 40, 40);
		endGameButtonRectangle = new Rectangle(screenWidth-tileWidth/2+10, screenHeight-tileHeight*2+30, 40, 40);
		
		//user input and OS shutdown hook
		readyForTermination();
		addInputListeners();
		
		//color list for adding new players
		colorList.add(Color.red);
		colorList.add(Color.blue);
		colorList.add(Color.green);
		colorList.add(Color.white);
		colorList.add(Color.yellow);
		colorIterator = colorList.iterator();
		
		//interface to the game model
		gameController = new GameController();
	    
	    startGame();
	}
	
	//starts the animation thread if it's not already running
	private void startGame()
	{
		if(animator == null || !running)
		{
			animator = new Thread(this);
			animator.start();
		}
	}

	//breaks the animation thread loop in run()
	public void stopGame()
	{
		running = false;
	}

	private void addInputListeners() {
		//mouse clicks
		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{ handleMousePress(e.getX(), e.getY(), e); }
		});

		//mouse movement (for landscape scrolling)
		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{ handleMouseMove(e.getX(), e.getY()); }
		});

		//keep focus system from catching tab key
		this.setFocusTraversalKeysEnabled(false);

		//key listener for scores display button
		addKeyListener( new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					running = false;
				else if(e.getKeyCode() == KeyEvent.VK_TAB)
					showScores = true;
			}
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
					showScores = false;
			}
			public void keyTyped(KeyEvent e)
			{ 
				if(gameController.isGameStarting() && colorIterator.hasNext())
				{
					if(e.getKeyChar() == '\n' && !newPlayerName.isEmpty())
					{
						gameController.addPlayer(newPlayerName, colorIterator.next());
						newPlayerName = new String();
					}
					else if(e.getKeyChar() == 8 && !newPlayerName.isEmpty())
						newPlayerName = newPlayerName.substring(0, newPlayerName.length()-1);
					else
						newPlayerName += e.getKeyChar();
				}
			}
		});
	}

	public void run()
	{
		running = true;
		while(running)
		{
			//gameUpdate();
			gameRender();
			paintScreen();

			try
			{
				Thread.sleep(20);
			}
			catch(InterruptedException e)
			{
				System.out.println(e);
			}
		}
		System.exit(0);
	}

	//update animation stuff here
	//currently there are no animations
//	private void gameUpdate(){	}

	//draw the game into the double buffer image
	private void gameRender()
	{
		//initialize Image and Graphics2D objects for double buffering
		if(dbImage == null)
		{
			dbImage = createImage(screenWidth, screenHeight);
			if(dbImage == null)
			{
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D)dbImage.getGraphics();
		}

		//clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,screenWidth,screenHeight);

		//adjust origin translation if mouse at edge of frame
		if(this.hasFocus() && !gameController.isGameStarting())
		{
			if(northScrollFlag)
				transY += 4;
			if(southScrollFlag)
				transY -= 4;
			if(eastScrollFlag)
				transX -= 4;
			if(westScrollFlag)
				transX += 4;
		}

		//translate based on transX/Y and paint the landscape
		if(!gameController.isGameStarting())
			paintLandscape(dbg);

		paintHUDbackground();
		
		//paint the buttons, player info, etc
		if(!gameController.isGameStarting() && !gameController.isGameOver())
			paintHUD();

		//display the new game setup screen
		if(gameController.isGameStarting())
			displayStartScreen();

		//display the scores overlay
		if(showScores && !gameController.isGameOver() && !gameController.isGameStarting())
			displayScores();
		
		//display the game over screen
		if(gameController.isGameOver())
			displayGameOverScreen();
	}
	
	//copy the double buffer image to the screen
	private void paintScreen()
	{
		Graphics g;
		try
		{
			g = this.getGraphics();
			if(g != null && dbImage != null)
				g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}
		catch(Exception e)
		{
			System.out.println("Error painting screen: \n " + e);
		}
	}

	private void paintHUDbackground() {
		//draw hud background
		dbg.setColor(Color.DARK_GRAY);
		dbg.fillRect(screenWidth-tileWidth/2, 0, screenWidth, screenHeight);
		dbg.fillRect(0,0, screenWidth, tileHeight/2);
	}

	private void paintHUD()
	{
		//draw done button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fill(doneButtonRectangle);
		dbg.setColor(Color.black);
		dbg.setFont(new Font("Serif", Font.BOLD, 14));
		dbg.drawString("Done", doneButtonRectangle.x + 5, doneButtonRectangle.y + 25);
		
		//draw end game button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fill(endGameButtonRectangle);
		dbg.setColor(Color.black);
		dbg.setFont(new Font("Serif", Font.BOLD, 14));
		dbg.drawString("End", endGameButtonRectangle.x + 5, endGameButtonRectangle.y + 15);
		dbg.drawString("Game", endGameButtonRectangle.x + 3, endGameButtonRectangle.y + 30);

		//draw peek at next tile
		dbg.translate(screenWidth - tileWidth + 2, screenHeight - tileHeight + 2);
		dbg.setColor(Color.black);
		dbg.setStroke(new BasicStroke(4));
		dbg.draw(peekRectangle);
		BufferedImage img = gameController.getNextTileImage();
		if(img != null)  //fill black rectangle if no next tile
			dbg.drawImage(img, 0, 0, null);
		else
			dbg.fill(peekRectangle);
		dbg.translate(-(screenWidth-tileWidth+2), -(screenHeight-tileHeight+2));

		//draw player name
		dbg.setPaint(gameController.getCurrentPlayerColor());
		dbg.setFont(new Font("SansSerif", Font.BOLD, 18));
		dbg.drawString(gameController.getCurrentPlayerName(), screenWidth/3, 30);

		//draw player tokens
		dbg.translate(screenWidth-tileWidth/2+20, 20);
		dbg.setPaint(gameController.getCurrentPlayerColor());
		int tokenCount = gameController.getCurrentPlayerTokenCount();
		int tokenOffset = (screenHeight-tileHeight*2-40) / 8;
		for(int i = 0; i < tokenCount; i++)
		{
			dbg.fillOval(0, i*tokenOffset, 30, 30);
		}
		dbg.translate(-(screenWidth-tileWidth/2+20), -20);
	}

	private void displayScores()
	{
		dbg.translate(screenWidth/6, screenHeight/6);

		int scoresWidth = screenWidth/6*4;
		int scoresHeight = screenHeight/6*4;
		dbg.setColor(new Color(100,100,100,200));
		dbg.fillRect(0, 0, scoresWidth, scoresHeight);
		dbg.setColor(Color.black);
		dbg.drawRect(0, 0, scoresWidth, scoresHeight);


		//draw player names and scores
		Iterator<Player> playersIterator = gameController.getPlayersIterator();
		for(int i = 1; playersIterator.hasNext(); i++)
		{
			Player player = playersIterator.next();
			dbg.setPaint(player.getColor());
			dbg.setFont(new Font("SansSerif", Font.BOLD, 18));
			dbg.drawString(player.getName(), 10, 30*i);
			dbg.drawString(Integer.toString(player.getScore()), scoresWidth/2, 30*i);
		}

		dbg.translate(-screenWidth/6, -screenHeight/6);
	}

	public void displayStartScreen()
	{
		//draw start game button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fill(endGameButtonRectangle);
		dbg.setColor(Color.black);
		dbg.setFont(new Font("Serif", Font.BOLD, 14));
		dbg.drawString("Start", endGameButtonRectangle.x + 5, endGameButtonRectangle.y + 15);
		dbg.drawString("Game", endGameButtonRectangle.x + 3, endGameButtonRectangle.y + 30);
		
		//set origin to upper left of start screen box
		dbg.translate(screenWidth/6, screenHeight/6);

		int scoresWidth = screenWidth/6*4;
		int scoresHeight = screenHeight/6*4;
		dbg.setColor(new Color(100,100,100,200));
		dbg.fillRect(0, 0, scoresWidth, scoresHeight);
		dbg.setColor(Color.black);
		dbg.drawRect(0, 0, scoresWidth, scoresHeight);

		//draw player name prompt
		dbg.setPaint(Color.black);
		dbg.setFont(new Font("SansSerif", Font.BOLD, 18));
		dbg.drawString("Enter player name: " + newPlayerName, 10, 30);
		
		//draw player names and scores
		Iterator<Player> playersIterator = gameController.getPlayersIterator();
		for(int i = 2; playersIterator.hasNext(); i++)
		{
			Player player = playersIterator.next();
			dbg.setPaint(player.getColor());
			dbg.setFont(new Font("SansSerif", Font.BOLD, 18));
			dbg.drawString(player.getName(), 20, 30*i);
		}

		dbg.translate(-screenWidth/6, -screenHeight/6);
	}
	
	public void displayGameOverScreen()
	{
		displayScores();

		//draw new game button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fill(endGameButtonRectangle);
		dbg.setColor(Color.black);
		dbg.setFont(new Font("Serif", Font.BOLD, 14));
		dbg.drawString("New", endGameButtonRectangle.x + 5, endGameButtonRectangle.y + 15);
		dbg.drawString("Game", endGameButtonRectangle.x + 3, endGameButtonRectangle.y + 30);
	}

	public void paintLandscape(Graphics g)
	{
		//translate for landscape scrolling
		dbg.translate(transX + screenWidth/2 - tileWidth/2, transY + screenHeight/2 - tileHeight/2);

		//iterate through placed tiles, drawing relative to translated origin
		Iterator<Tile> landscapeIterator = gameController.getLandscapeIterator();
		while(landscapeIterator.hasNext())
		{
			Tile t = landscapeIterator.next();
			g.drawImage(t.getImage(),(t.getPoint().x)*tileWidth, -(t.getPoint().y)*tileHeight, null);
			if(t.hasToken())
			{
				g.setColor(t.getToken().getColor());
				Point tokenCoordinates = t.getTokenCoordinates();
				int tokenX = tokenCoordinates.x-10;
				int tokenY = tokenCoordinates.y-10;
				g.fillOval((t.getPoint().x)*tileWidth+tokenX, -(t.getPoint().y)*tileHeight+tokenY, 20, 20);
				g.setColor(Color.BLACK);
				g.drawOval((t.getPoint().x)*tileWidth+tokenX, -(t.getPoint().y)*tileHeight+tokenY, 20, 20);
			}
		}
		
		//draw box around last tile to ease token placement
		Point lastTileCoords = gameController.getLastTileCoords();
		dbg.translate((lastTileCoords.x)*tileWidth, -(lastTileCoords.y)*tileHeight);
		dbg.setColor(Color.black);
		dbg.draw(peekRectangle);
		dbg.translate(-(lastTileCoords.x)*tileWidth, (lastTileCoords.y)*tileHeight);

		//translate back from landscape scrolling
		dbg.translate(-(transX + screenWidth/2 - tileWidth/2), -(transY + screenHeight/2 - tileHeight/2));
	}

	//sets up OS shutdown hook
	private void readyForTermination()
	{
		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{ 
				running = false;
			}
		});
	}

	//evaluates mouse clicks
	private void handleMousePress(int xInPanel, int yInPanel, MouseEvent e)
	{
		//check for rotate attempt
		if(e.getButton() == MouseEvent.BUTTON3)
			gameController.rotateNextTile();
		//check done button
		else if(doneButtonRectangle.contains(xInPanel, yInPanel))
			gameController.endTurn();
		//check new/end game button
		else if(endGameButtonRectangle.contains(xInPanel, yInPanel))
		{
			if(gameController.isGameOver())
			{
				gameController.newGame();
				colorIterator = colorList.iterator(); //reset colors iterator
			}
			else if(gameController.isGameStarting())
				gameController.startGame();
			else
				gameController.endGame();
		}
		//check for landscape click
		else if(xInPanel < screenWidth-40 && yInPanel > tileHeight/2)
		{
			//remove offset due to landscape scrolling/centering translation
			xInPanel = xInPanel - transX - screenWidth/2 + tileWidth/2;
			yInPanel = yInPanel - transY - screenHeight/2 + tileHeight/2;

			//calc coords of tile in model space
			int xInModel = (int) Math.floor((double)xInPanel / tileWidth);
			int yInModel = (int) -Math.floor((double)yInPanel / tileHeight);

			//calc pixel coords within tile
			int xInTile = xInPanel % tileWidth;
			int yInTile = yInPanel % tileHeight;
			xInTile = xInTile >= 0 ? xInTile : xInTile + tileWidth; //compensates for negative operands to %
			yInTile = yInTile >= 0 ? yInTile : yInTile + tileHeight;

			gameController.handleLandscapeClick(xInModel, yInModel, xInTile, yInTile);
		}
	}

	//evaluates mouse movements/location
	//triggers landscape scrolling at edges of panel
	private void handleMouseMove(int x, int y)
	{ 
		if(x > screenWidth - 10)
			eastScrollFlag = true;
		else
			eastScrollFlag = false;

		if(x < 10)
			westScrollFlag = true;
		else
			westScrollFlag = false;

		if(y < 10)
			northScrollFlag = true;
		else
			northScrollFlag = false;

		if(y > screenHeight - 10)
			southScrollFlag = true;
		else
			southScrollFlag = false;
	}
}
