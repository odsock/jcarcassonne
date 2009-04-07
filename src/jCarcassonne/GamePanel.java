package jCarcassonne;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{

	private static final long serialVersionUID = 1L;
	//JPanel dimensions
	private int panelWidth;
	private int panelHeight;

	//tile image dimensions
	private final int tileWidth;
	private final int tileHeight;

	//game model elements
	private Rules rules;
	private TileStack tileStack;
	private Landscape landscape;
	private ArrayList<Player> players = new ArrayList<Player>();
	private Iterator<Player> playersIterator;
	private Player currentPlayer;
	private boolean tilePlacedThisTurn = false;

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	//private volatile boolean gameOver = false;

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
			{ testMousePress(e.getX(), e.getY(), e); }
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{ testMouseMove(e.getX(), e.getY()); }
		});


		//create the game model
		rules = new Rules();
		rules.setVerbose(true);
		tileStack = new TileStack();
		tileStack.loadTileSet("tileset.txt");
		tileStack.shuffleStack();
		tileWidth = tileStack.getTileWidth();
		tileHeight = tileStack.getTileHeight();
		landscape = new Landscape(tileStack.pop());
		players.add(new Player("player1", Color.red));
		players.add(new Player("player2", Color.blue));
		playersIterator = players.iterator();
		currentPlayer = playersIterator.next();
	}

	public void run()
	{
		running = true;
		while(running)
		{
			gameUpdate();
			gameRender();
			repaint();

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

		if(rules.checkTilePlacement(landscape, tileStack.peek(),x,y))
		{
			landscape.placeTile(tileStack.pop(), x,y);
			return true;
		}
		else
			return false;
	}

	//draw the game into the double buffer image
	private void gameRender()
	{
		//initialize Image and Graphics2D objects for double buffering
		if(dbImage == null)
		{
			dbImage = createImage(panelWidth, panelHeight);
			if(dbImage == null)
			{
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
		paintLandscape(dbg);
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
			dbg.draw(new Rectangle(tileWidth,tileHeight));
			dbg.drawImage(tileStack.peek().getImage(), 0, 0, null);
			dbg.translate(-(panelWidth - tileWidth + 2), -(panelHeight - tileHeight + 2));
		}
	}

	public void paintLandscape(Graphics g)
	{
		Iterator<Tile> landscapeIterator = landscape.getLandscapeIterator();
		while(landscapeIterator.hasNext())
		{
			Tile t = landscapeIterator.next();
			g.drawImage(t.getImage(),(t.getPoint().x)*128, -(t.getPoint().y)*128, null);
			if(t.hasToken())
			{
				g.setColor(t.getToken().getColor());
				g.fillOval((t.getPoint().x)*tileWidth+t.getTokenX(), -(t.getPoint().y)*tileHeight+t.getTokenY(), 20, 20);
			}
		}
	}

	//copy double buffer image to the screen
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(dbImage != null)
			g.drawImage(dbImage,0,0,null);
	}

	//starts the animation loop when GamePanel is added to GameFrame
	public void addNotify()
	{
		super.addNotify();
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

	//sets up keyboard quit keys and OS shutdown hook
	private void readyForTermination()
	{
		//keyboard listeners
		addKeyListener( new KeyAdapter() {
			// listen for escape key
			public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ESCAPE)
				{
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
	private void testMousePress(int xInPanel, int yInPanel, MouseEvent e)
	{
		//if Right mouse button, rotate next tile
		if(e.getButton() == MouseEvent.BUTTON3 && !tilePlacedThisTurn)
		{
			tileStack.peek().rotate();
			return;
		}

		//check Done button
		if(xInPanel >= panelWidth-tileWidth/2+10 && xInPanel <= panelWidth-tileWidth/2+10+40 &&
				yInPanel >= panelHeight-tileHeight*2 && yInPanel <= panelHeight-tileHeight*2+30)
		{
			endTurn();
			return;
		}

		//remove offset due to landscape scrolling/centering translation
		xInPanel = xInPanel - transX - panelWidth/2 + tileWidth/2;
		yInPanel = yInPanel - transY - panelHeight/2 + tileHeight/2;

		//calc coords of tile in model space
		int xInModel = (int) Math.floor((double)xInPanel / tileWidth);
		int yInModel = (int) -Math.floor((double)yInPanel / tileHeight);

		//place tile if rules allow
		if(!tilePlacedThisTurn && rules.checkTilePlacement(landscape, tileStack.peek(), xInModel, yInModel))
		{
			landscape.placeTile(tileStack.pop(), xInModel, yInModel);
			tilePlacedThisTurn = true;

			System.out.println("GamePanel: tile placed");
			return;
		}

		//calc pixel coords within tile
		int xInTile = xInPanel % tileWidth;
		int yInTile = yInPanel % tileHeight;
		xInTile = xInTile > 0 ? xInTile : xInTile + tileWidth; //compensates for negative operands to %
		yInTile = yInTile > 0 ? yInTile : yInTile + tileHeight;

		//place token if rules allow
	//	if(tilePlacedThisTurn && rules.checkTokenPlacement(landscape, currentPlayer, xInModel, yInModel, xInTile, yInTile))
		if(rules.checkTokenPlacement(landscape, currentPlayer, xInModel, yInModel, xInTile, yInTile))
		{
			Token token = currentPlayer.getToken();
			landscape.placeToken(token, xInTile, yInTile);
			token.setPlaced(true);
			endTurn();

			System.out.println("GamePanel: token placed");
			return;
		}

		System.out.println("GamePanel: Unhandled click.");
	}

	private void endTurn()
	{
		tilePlacedThisTurn = false;

		if(playersIterator.hasNext())
			currentPlayer = playersIterator.next();
		else
		{
			playersIterator = players.iterator();
			currentPlayer = playersIterator.next();
		}
	}

	//evaluates mouse movements/location
	//triggers landscape scrolling at edges of panel
	private void testMouseMove(int x, int y)
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