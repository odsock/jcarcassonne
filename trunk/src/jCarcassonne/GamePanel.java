package jCarcassonne;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable
{
	private static final long serialVersionUID = 1L;
	//JPanel dimensions
	private int panelWidth;
	private int panelHeight;

	//tile image dimensions
	private final int tileWidth = 128;
	private final int tileHeight = 128;

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;

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

	//interface to game model
	private GameController gameController;

	public GamePanel(int pw, int ph)
	{
		gameController = new GameController();

		this.panelWidth = pw;
		this.panelHeight = ph;

		setBackground(Color.white);
		setPreferredSize( new Dimension(panelWidth, panelHeight));
		
		doneButtonRectangle = new Rectangle(panelWidth-tileWidth/2+10, panelHeight-tileHeight*2-20, 40, 40);
		endGameButtonRectangle = new Rectangle(panelWidth-tileWidth/2+10, panelHeight-tileHeight*2+30, 40, 40);

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
		
/*		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension cursorDim = toolkit.getBestCursorSize(40, 40);
		BufferedImage tokenCursor = new BufferedImage(cursorDim.width, cursorDim.height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D cursorGraphics = tokenCursor.createGraphics();
		cursorGraphics.setColor(gameController.getCurrentPlayerColor());
		cursorGraphics.fillOval(0, 0, cursorDim.width-1, cursorDim.height-1);
		setCursor(toolkit.createCustomCursor(tokenCursor, new Point(0,0), "tokenCursor"));*/
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

		//paint the buttons, player info, etc
		paintHUD();
	}

	private void paintHUD() {
		//draw hud background
		dbg.setColor(Color.DARK_GRAY);
		dbg.fillRect(panelWidth-tileWidth/2, 0, panelWidth, panelHeight);
		dbg.fillRect(0,0, panelWidth, 40);

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
		dbg.drawString("End Game", endGameButtonRectangle.x + 5, endGameButtonRectangle.y + 25);
		
		//draw peek at next tile
		dbg.translate(panelWidth - tileWidth + 2, panelHeight - tileHeight + 2);
		dbg.setColor(Color.black);
		dbg.setStroke(new BasicStroke(4));
		dbg.draw(peekRectangle);
		BufferedImage img = gameController.getNextTileImage();
		if(img != null)
			dbg.drawImage(img, 0, 0, null);
		else
			dbg.fill(peekRectangle);
		dbg.translate(-(panelWidth-tileWidth+2), -(panelHeight-tileHeight+2));
		
		//draw player name and score
		dbg.setPaint(gameController.getCurrentPlayerColor());
		dbg.setFont(new Font("SansSerif", Font.BOLD, 18));
		dbg.drawString(gameController.getCurrentPlayerName() + ":   Score=" + Integer.toString(gameController.getCurrentPlayerScore()), panelWidth/3, 30);
		
		//draw player tokens
		dbg.translate(panelWidth-tileWidth/2+20, 20);
		dbg.setPaint(gameController.getCurrentPlayerColor());
		int tokenCount = gameController.getCurrentPlayerTokenCount();
		int tokenOffset = (panelHeight-tileHeight*2-40) / 8;
		for(int i = 0; i < tokenCount; i++)
		{
			dbg.fillOval(0, i*tokenOffset, 30, 30);
		}
		dbg.translate(-(panelWidth-tileWidth/2+20), -20);
	}

	public void paintLandscape(Graphics g)
	{
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
		//check for rotate attempt
		if(e.getButton() == MouseEvent.BUTTON3)
			gameController.rotateNextTile();
		//check done button
		else if(doneButtonRectangle.contains(xInPanel, yInPanel))
			gameController.endTurn();
		//check end game button
		else if(endGameButtonRectangle.contains(xInPanel, yInPanel))
			gameController.endGame();
		//check for landscape click
		else if(xInPanel < panelWidth-40 && yInPanel > tileWidth/2)
		{
			//remove offset due to landscape scrolling/centering translation
			xInPanel = xInPanel - transX - panelWidth/2 + tileWidth/2;
			yInPanel = yInPanel - transY - panelHeight/2 + tileHeight/2;

			//calc coords of tile in model space
			int xInModel = (int) Math.floor((double)xInPanel / tileWidth);
			int yInModel = (int) -Math.floor((double)yInPanel / tileHeight);

			//calc pixel coords within tile
			int xInTile = xInPanel % tileWidth;
			int yInTile = yInPanel % tileHeight;
			xInTile = xInTile > 0 ? xInTile : xInTile + tileWidth; //compensates for negative operands to %
			yInTile = yInTile > 0 ? yInTile : yInTile + tileHeight;

			gameController.handleLandscapeClick(xInModel, yInModel, xInTile, yInTile);
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
