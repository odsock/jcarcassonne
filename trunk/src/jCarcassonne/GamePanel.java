package jCarcassonne;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{

	private Stack<Tile> tileStack;
	private Landscape landscape;
	private Player[] players = new Player[2];  //change this later to accommodate more players

	//animation loop stuff
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;

	//double buffering stuff
	private Graphics dbg;
	private Image dbImage = null;

	//game update stuff
	private int count = 0;

	public GamePanel()
	{
		setBackground(Color.white);
		setPreferredSize( new Dimension(800, 600));

		setFocusable(true);
		requestFocus();    // the JPanel now has focus, so receives key events

		readyForTermination();

		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{ testPress(e.getX(), e.getY()); }
		});

		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e)
			{ testMove(e.getX(), e.getY()); }
		});

		fillStack();
		landscape = new Landscape(tileStack.pop());
	}

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
	private void testPress(int x, int y)
	{

	}

	//evaluates mouse movements/location
	private void testMove(int x, int y)
	{ 

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
				Thread.sleep(5);
			}
			catch(InterruptedException e)
			{
				System.out.println(e);
			}
		}
	}

	private void gameUpdate()
	{
		if(!gameOver && !tileStack.empty())
		{
			Random rand = new Random();
			int x = rand.nextInt(10)-5;
			int y = rand.nextInt(10)-5;

			if(Rules.checkTilePlacement(landscape, tileStack.peek(),x,y))
				landscape.placeTile(tileStack.pop(), x,y);
			else
			{
				count++;
				if(count > 9)
				{
					tileStack.pop();
					count = 0;
				}
			}
		}
		else
			stopGame();
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
				dbg = dbImage.getGraphics();
		}

		//clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,800,600);

		//draw the landscape
		landscape.paintLandscape(dbg);
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
		GamePanel game = new GamePanel();

		//load and randomize the tileset
		game.fillStack();

		//seed the landscape with the start tile
		game.landscape = new Landscape(game.tileStack.pop());

		//add some more tiles for testing
		Random rand = new Random();
		int i = 0;
		int j = 0;
		while(i < 1000 && !game.tileStack.empty())
		{
			int x = rand.nextInt(10);
			int y = rand.nextInt(10);
			if(Rules.checkTilePlacement(game.landscape, game.tileStack.peek(),x,y))
			{
				game.landscape.placeTile(game.tileStack.pop(), x,y);
				j = 0;
			}
			i++;
			j++;
			if(j > 9)
			{
				game.tileStack.pop();
				//				System.out.println("pop");
				j = 0;
			}
		}

		//setup the players
		game.players[0] = new Player("Player1", Color.red);
		game.players[1] = new Player("Player2", Color.blue);

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

		//try to display some stuff
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel(new BorderLayout());
		p.add(game.landscape, BorderLayout.CENTER);
		f.add(p);
		f.pack();
		f.setVisible(true);
	}*/

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
}