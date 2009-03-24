package jCarcassonne;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//JPanel dimensions
	int panelWidth;
	int panelHeight;

	//tile image dimensions
	int tileWidth = 128;
	int tileHeight = 128;

	//game model elements
	private Rules rules;
	private TileStack tileStack;
	private Landscape landscape;
	//private LinkedList<Player> players = new LinkedList<Player>();  //change this later to accommodate more players
	private boolean tilePlaced = false;

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	//private volatile boolean gameOver = false;

	//double buffering stuff
	private Graphics2D dbg;
	private Image dbImage = null;

	//landscape scrolling stuff
	int transX = 0;
	int transY = 0;
	private boolean northScrollFlag = false;
	private boolean southScrollFlag = false;
	private boolean eastScrollFlag = false;
	private boolean westScrollFlag = false;

	public GamePanel(int pw, int ph)
	{
		this.panelWidth = pw;
		this.panelHeight = ph;
		
		setBackground(Color.white);
		setPreferredSize( new Dimension(panelWidth, panelHeight));

		setFocusable(true);
		requestFocus();    // the JPanel now has focus, so receives key events

		readyForTermination();

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{ testPress(e.getX(), e.getY(), e); }
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{ testMove(e.getX(), e.getY()); }
		});
		
		//create the game model
		
		rules = new Rules();
		rules.setVerbose(false);
		
		tileStack = new TileStack();
		tileStack.loadTileSet("tileset.txt");
		tileStack.shuffleStack();
		
		landscape = new Landscape(tileStack.pop());
	}

	public void run()
	{
		running = true;
		while(running)
		{
			gameUpdate();
			gameRender();
			repaint();

			try{
				Thread.sleep(20);
			}
			catch(InterruptedException e){
				System.out.println(e);
			}
		}
		System.exit(0);
	}

	//update animation stuff here
	private void gameUpdate()
	{
	}
	
	//attempts to randomly place the next tile within 5 units of the origin
	@SuppressWarnings("unused")
	private boolean randomlyPlaceNextTile()
	{		
		Random rand = new Random();
		int x = rand.nextInt(10)-5;
		int y = rand.nextInt(10)-5;

		if(rules.checkTilePlacement(landscape, tileStack.peek(),x,y)) {
			landscape.placeTile(tileStack.pop(), x,y);
			
			return true;
		}
		else
			return false;
	}

	private void gameRender()
	{
		//initialize Image and Graphics2D objects for double buffering
		if(dbImage == null)
		{
			dbImage = createImage(panelWidth, panelHeight);
			if(dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D)dbImage.getGraphics();
		}

		//adjust origin translation if mouse at edge of frame
		if(northScrollFlag)
			transY += 4;
		if(southScrollFlag)
			transY -= 4;
		if(eastScrollFlag)
			transX -= 4;
		if(westScrollFlag)
			transX += 4;

		//clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,panelWidth,panelHeight);

		//draw the landscape
		dbg.translate(transX + panelWidth/2 - tileWidth/2, transY + panelHeight/2 - tileHeight/2);
		landscape.paintLandscape(dbg);
		dbg.translate(-(transX + panelWidth/2 - tileWidth/2), -(transY + panelHeight/2 - tileHeight/2));

		//draw hud background
		dbg.setColor(Color.DARK_GRAY);
		dbg.fillRect(panelWidth-tileWidth/2, 0, panelWidth, panelHeight);
		dbg.fillRect(0,0, panelWidth, 40);

		//draw done button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fillRect(panelWidth-tileWidth/2+10, panelHeight-tileHeight*2, 40, 30);
		dbg.setColor(Color.black);
		dbg.drawString("Done", panelWidth-tileWidth/2+10+5, panelHeight-tileHeight*2+25);

		//draw peek at next tile
		if(!tileStack.empty())
		{
			dbg.translate(panelWidth - tileWidth + 2, panelHeight - tileHeight + 2);
			dbg.setColor(Color.black);
			dbg.setStroke(new BasicStroke(4));
			dbg.draw(new Rectangle(128,128));
			dbg.drawImage(tileStack.peek().getImage(), 0, 0, null);
			dbg.translate(-(panelWidth - tileWidth + 2), -(panelHeight - tileHeight + 2));
		}
	}

	//copy double buffer image to the screen
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(dbImage != null)
			g.drawImage(dbImage,0,0,null);
	}

	//starts the animation loop when GamePanel is added to GameFrame
	public void addNotify()	{
		super.addNotify();
		startGame();
	}

	//starts the animation thread if it's not already running
	private void startGame()
	{
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	//breaks the animation threads loop in run()
	public void stopGame() {
		running = false;
	}

	//sets up keyboard quit keys and OS shutdown hook
	private void readyForTermination()
	{
		//keyboard listeners
		addKeyListener( new KeyAdapter() {
			// listen for escape key
			public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ESCAPE) {
					running = false;
				}
			}
		});

		// for shutdown tasks
		// a shutdown may not only come from the program
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{ 
				running = false;
				//finishOff();
			}
		});
	}

	//evaluates mouse clicks
	private void testPress(int x, int y, MouseEvent e)
	{
		//don't look in the empty stack
		if(tileStack.empty())
			return;

		//if Right mouse button, rotate next tile
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			tileStack.peek().rotate();
			return;
		}

		//check Done button
		if(x >= panelWidth-tileWidth/2+10 && x <= panelWidth-tileWidth/2+10+40 &&
				y >= panelHeight-tileHeight*2 && y <= panelHeight-tileHeight*2+30)
		{
			tilePlaced = false;
			return;
		}

		//if Left mouse button try to place next tile

		//remove offset due to landscape scrolling/centering
		x = x - transX - panelWidth/2 + tileWidth/2;
		y = y - transY - panelHeight/2 + tileHeight/2;
		
		//calc pixel coords within tile
		int xInTile = x % tileWidth;
		int yInTile = y % tileHeight;
		xInTile = xInTile > 0 ? xInTile : xInTile + tileWidth;
		yInTile = yInTile > 0 ? yInTile : yInTile + tileHeight;
		
		//calc coords of tile in model space
		x = (int) Math.floor((double)x / tileWidth);
		y = (int) -Math.floor((double)y / tileHeight);

		//place tile if rules allow
		if(!tilePlaced && landscape.getTile(x, y) == null)
		{
			if(rules.checkTilePlacement(landscape, tileStack.peek(), x, y))
			{
				landscape.placeTile(tileStack.pop(), x, y);
				tilePlaced = true;
				return;
			}
		}

		//try to place token if tile has been placed this turn already
		if(tilePlaced && landscape.getTile(x, y) != null)
		{
			if(rules.checkTokenPlacement(landscape, new Player("player1", Color.red), x, y, xInTile, yInTile))
			{
				landscape.placeToken();
				tilePlaced = false;
			}
		}
	}

	//evaluates mouse movements/location
	private void testMove(int x, int y)
	{ 
		if(x > panelWidth - 10)
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

		if(y > panelHeight - 10)
			southScrollFlag = true;
		else
			southScrollFlag = false;
	}
}