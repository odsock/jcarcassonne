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

	//game model elements
	private Stack<Tile> tileStack;
	private Landscape landscape;
	private Player[] players = new Player[2];  //change this later to accommodate more players

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;

	//double buffering stuff
	private Graphics2D dbg;
	private Image dbImage = null;

	//game update stuff
	private int numPlacementAttempts = 0;

	//mouse movement flags
	int tx = 0;
	int ty = 0;
	private boolean northFlag = false;
	private boolean southFlag = false;
	private boolean eastFlag = false;
	private boolean westFlag = false;

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

	private void gameUpdate()
	{
		if(!gameOver && !tileStack.empty())
		{

		}
		/*		//randomly try to place the next tile
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
			if(dbImage == null)
			{
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D)dbImage.getGraphics();
		}

		//translate if mouse at edge of frame
		if(northFlag)
			ty += 4;
		if(southFlag)
			ty -= 4;
		if(eastFlag)
			tx -= 4;
		if(westFlag)
			tx += 4;

		//clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,800,600);

		//draw the landscape
		dbg.translate(tx, ty);
		landscape.paintLandscape(dbg);
		dbg.translate(-tx, -ty);

		//draw peek at next tile
		dbg.translate(pw - 130, ph -130);
		dbg.setColor(Color.black);
		dbg.setStroke(new BasicStroke(4));
		dbg.draw(new Rectangle(128,128));
		dbg.drawImage(tileStack.peek().getImage(), 0, 0, null);

		dbg.translate(-(pw - 130), -(ph -130));
	}

	private void fillStack() {
		tileStack = new Stack<Tile>();

		ArrayList<Tile> templist = new ArrayList<Tile>();
		try{
			BufferedReader in = new BufferedReader(new FileReader("tileset.txt"));
			while(in.ready()){
				String filename = in.readLine().split(" ")[1];
				BufferedImage img = ImageIO.read(new File(filename));

				int count = Integer.parseInt(in.readLine().split(" ")[1]);

				in.readLine();

				Tile.Feature[] features = new Tile.Feature[5];
				for(int i = 0; i < 4; i++){
					features[i] = Tile.Feature.valueOf(in.readLine().split(" ")[1]);
				}
				in.readLine();

				for(int i = 0; i < count; i++)
					templist.add(new Tile(features[0], features[1], features[2], features[3], features[4], img, filename));
			}
		} 
		catch (IOException e) {
			System.out.println("Error reading tile set.\n" + e);
		}

		//randomize stack from list of tiles loaded
		//keep start tile at head of stack
		Tile starttile = templist.remove(0);
		while(!templist.isEmpty())
		{
			Random rand = new Random();
			int i = rand.nextInt(templist.size());
			tileStack.push(templist.remove(i));
		}
		tileStack.push(starttile);
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
		if(animator == null || !running)
		{
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
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			System.out.println("rotate");
			tileStack.peek().rotate();
		}
		else
		{
			x = x - tx - pw/2 + 64;
			y = y - ty - ph/2 + 64;
			if(x < 0)
				x -= 128;
			if(y < 0)
				y -= 128;
			x = x / 128;
			y = -y / 128;

			if(landscape.getTile(x, y) != null)
				System.out.println("hit " + x + " " + y);
			else
			{
				System.out.println("miss " + x + " " + y);
				if(Rules.checkTilePlacement(landscape, tileStack.peek(), x, y))
					landscape.placeTile(tileStack.pop(), x, y);
				else
					System.out.println("    bad placement");
			}
		}
	}

	//evaluates mouse movements/location
	private void testMove(int x, int y)
	{ 
		if(x > pw - 20)
			eastFlag = true;
		else
			eastFlag = false;

		if(x < 20)
			westFlag = true;
		else
			westFlag = false;

		if(y < 20)
			northFlag = true;
		else
			northFlag = false;

		if(y > ph - 20)
			southFlag = true;
		else
			southFlag = false;
	}
}