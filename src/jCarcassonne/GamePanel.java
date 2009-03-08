package jCarcassonne;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

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
		x = x - tx;
		y = y - ty;
		if(x < 0)
			x -= 128;
		if(y < 0)
			y -= 128;
		x = x / 128;
		y = -y / 128;
		
		if(landscape.getTile(x, y) != null)
			System.out.println("hit " + x + " " + y);
		else
			System.out.println("miss " + x + " " + y);
	}

	//evaluates mouse movements/location
	private void testMove(int x, int y)
	{ 
		int pw = this.getWidth();
		int ph = this.getHeight();
		
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
			Random rand = new Random();
			int x = rand.nextInt(10)-5;
			int y = rand.nextInt(10)-5;

			if(Rules.checkTilePlacement(landscape, tileStack.peek(),x,y))
			{
				landscape.placeTile(tileStack.pop(), x,y);
				
			}
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
				dbg = dbImage.getGraphics();
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