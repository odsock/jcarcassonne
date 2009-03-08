package jCarcassonne;

import javax.swing.JFrame;

public class GameFrame extends JFrame{
	
	public GameFrame() {
		super("JCarcassonne");
		add(new GamePanel());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    pack();
	    setResizable(true);
	    setVisible(true);
	}
	
	public static void main(String[] args)
	{
		new GameFrame();
	}
}
