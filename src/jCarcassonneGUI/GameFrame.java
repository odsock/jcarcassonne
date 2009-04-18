package jCarcassonneGUI;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameFrame() {
		super("JCarcassonne");
		add(new GamePanel(800, 600));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    pack();
	    setResizable(true);
	    setLocationRelativeTo(null);  //start frame at center of screen
	    setVisible(true);
	}
	
	public static void main(String[] args)
	{
		new GameFrame();
	}
}
