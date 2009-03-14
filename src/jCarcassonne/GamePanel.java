package jCarcassonne;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
	//JPanel values
	int pw = 800;
	int ph = 600;

	int tw = 128;
	int th = 128;

	//game model elements
	private Stack<Tile> tileStack;
	private Landscape landscape;
	private LinkedList<Player> players = new LinkedList<Player>();  //change this later to accommodate more players
	private boolean tilePlaced = false;

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;

	//double buffering stuff
	private Graphics2D dbg;
	private Image dbImage = null;

	//mouse movement flags
	int tx = 0;
	int ty = 0;
	private boolean northScrollFlag = false;
	private boolean southScrollFlag = false;
	private boolean eastScrollFlag = false;
	private boolean westScrollFlag = false;

	public GamePanel()
	{
		setBackground(Color.white);
		setPreferredSize( new Dimension(pw, ph));

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

		fillStack();
		landscape = new Landscape(tileStack.pop());
	}

	public void run()
	{
		running = true;
		while(running)
		{
			//gameUpdate();
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

	private void gameUpdate()
	{
		if(!gameOver && !tileStack.empty())
		{

		}
		/*//code to randomly try to place the next tile within -5 to 5 square
		if(!gameOver && !tileStack.empty())
		{
			Random rand = new Random();
			int x = rand.nextInt(10)-5;
			int y = rand.nextInt(10)-5;

			if(Rules.checkTilePlacement(landscape, tileStack.peek(),x,y)) {
				landscape.placeTile(tileStack.pop(), x,y);
			}
			else
			{
				numPlacementAttempts++;
				if(numPlacementAttempts > 9) {
					tileStack.pop();
					numPlacementAttempts = 0;
				}
			}
		}*/
		else
			gameOver = true;
	}

	private void gameRender()
	{
		if(dbImage == null)
		{
			dbImage = createImage(800, 600);
			if(dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D)dbImage.getGraphics();
		}

		//translate if mouse at edge of frame
		if(northScrollFlag)
			ty += 4;
		if(southScrollFlag)
			ty -= 4;
		if(eastScrollFlag)
			tx -= 4;
		if(westScrollFlag)
			tx += 4;

		//clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,pw,ph);

		//draw the landscape
		dbg.translate(tx + pw/2 - tw/2, ty + ph/2 - th/2);
		landscape.paintLandscape(dbg);
		dbg.translate(-(tx + pw/2 - tw/2), -(ty + ph/2 - th/2));

		//draw hud background
		dbg.setColor(Color.DARK_GRAY);
		dbg.fillRect(pw-tw/2, 0, pw, ph);
		dbg.fillRect(0,0, pw, 40);

		//draw done button
		dbg.setColor(Color.LIGHT_GRAY);
		dbg.fillRect(pw-tw/2+10, ph-th*2, 40, 30);
		dbg.setColor(Color.black);
		dbg.drawString("Done", pw-tw/2+10+5, ph-th*2+25);

		//draw peek at next tile
		if(!tileStack.empty())
		{
			dbg.translate(pw - tw + 2, ph - th + 2);
			dbg.setColor(Color.black);
			dbg.setStroke(new BasicStroke(4));
			dbg.draw(new Rectangle(128,128));
			dbg.drawImage(tileStack.peek().getImage(), 0, 0, null);
			dbg.translate(-(pw - tw + 2), -(ph - th + 2));
		}
	}

	private void fillStack() {
		tileStack = new Stack<Tile>();

		//fill list with tiles first
		ArrayList<Tile> templist = new ArrayList<Tile>();
		String filename = "";
		Tile startTile = null;
		try{
			BufferedReader in = new BufferedReader(new FileReader("tileset.txt"));
			while(in.ready()){
				//read image file name, then load image
				filename = in.readLine().split(" ")[1];
				BufferedImage img = ImageIO.read(new File(filename));

				//read tile count
				int count = Integer.parseInt(in.readLine().split(" ")[1]);

				//skip flag for now
				in.readLine();

				//read five tile features
				Tile.Feature[] features = new Tile.Feature[5];
				for(int i = 0; i < 5; i++){
					String t = in.readLine();
					String[] temp = t.split(" ");
					features[i] = Tile.Feature.valueOf(temp[1]);
				}

				//skip blank line
				in.readLine();

				//catch start tile so it can be placed on top of the tile stack
				if(filename.equals("startTile.jpg"))
					startTile = new Tile(features[0], features[1], features[2], features[3], features[4], img, filename);
				else
				{
					//create the tiles
					for(int i = 0; i < count; i++)
						templist.add(new Tile(features[0], features[1], features[2], features[3], features[4], img, filename));
				}
			}
		} 
		catch (IOException e) {
			System.out.println("Error reading tileset at " + filename + ".\n" + e);
		}

		//randomize stack from list of tiles loaded
		//keep start tile at head of stack
		while(!templist.isEmpty())
		{
			Random rand = new Random();
			int i = rand.nextInt(templist.size());
			tileStack.push(templist.remove(i));
		}
		if(startTile != null)  //start tile is identified by image file name
			tileStack.push(startTile);
		else
			System.out.println("startTile.jpg not found!");
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(dbImage != null)
			g.drawImage(dbImage,0,0,null);
	}

	public void addNotify()	{
		super.addNotify();
		startGame();
	}

	private void startGame()
	{
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public void stopGame() {
		running = false;
	}

	/*public static void main2(String[] args) {

		//seed the landscape with the start tile
		game.landscape = new Landscape(game.tileStack.pop());

		//setup the window
		JWindow w = new JWindow();
		JRootPane rp = w.getRootPane();

		Action quit = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		rp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F2"), "quit");
		rp.getActionMap().put("quit", quit);


		//try to do fullscreen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if(gd.isFullScreenSupported())
		{
			try
			{
				gd.setFullScreenWindow(w);
				w.add(game.landscape);
				w.pack();
				w.setSize(1024, 768);
				w.getFocusOwner();
				w.setVisible(true);
			}
			finally
			{
				//gd.setFullScreenWindow(null);
			}
		}
	}*/

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
		if(x >= pw-tw/2+10 && x <= pw-tw/2+10+40 &&
				y >= ph-th*2 && y <= ph-th*2+30)
		{
			tilePlaced = false;
			return;
		}

		//if Left mouse button try to place next tile
		//calculate tile coordinates in model space
		x = x - tx - pw/2 + 64;
		y = y - ty - ph/2 + 64;
		if(x < 0)
			x -= 128;
		if(y < 0)
			y -= 128;
		x = x / 128;
		y = -y / 128;

		//place if rules allow
		if(!tilePlaced && landscape.getTile(x, y) == null)
		{
			if(Rules.checkTilePlacement(landscape, tileStack.peek(), x, y))
			{
				landscape.placeTile(tileStack.pop(), x, y);
				tilePlaced = true;
				return;
			}
		}

		if(tilePlaced && landscape.getTile(x, y) != null)
		{
			if(Rules.checkTokenPlacement(landscape, new Player("player1", Color.red), x, y))
			{
				landscape.placeToken(x,y);
				tilePlaced = false;
			}
		}
	}

	//evaluates mouse movements/location
	private void testMove(int x, int y)
	{ 
		if(x > pw - 10)
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

		if(y > ph - 10)
			southScrollFlag = true;
		else
			southScrollFlag = false;
	}
}