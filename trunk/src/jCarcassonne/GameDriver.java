package jCarcassonne;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameDriver {

	Stack<Tile> tileStack = new Stack<Tile>();
	Landscape landscape;
	Player[] players = new Player[2];  //change this later to accommodate more players

	public static void main(String[] args) {
		GameDriver game = new GameDriver();
		
		//load and randomize the tileset
		game.fillStack();
		
		//seed the landscape with the start tile
		Tile temp = game.tileStack.pop();
		System.out.println(temp);
		game.landscape = new Landscape(temp);
		
		//setup the players
		game.players[0] = new Player("Player1", Color.red);
		game.players[1] = new Player("Player2", Color.blue);
		
		//try to display some stuff
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		f.add(p);
		ImageIcon ii = new ImageIcon(game.tileStack.pop().getImage());
		p.add(new JLabel(ii));
		
		f.setVisible(true);
	}
	private void fillStack() {
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
					templist.add(new Tile(features[0], features[1], features[2], features[3], features[4], img));
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
			int i = (int)(Math.random() * templist.size());
			tileStack.push(templist.remove(i));
		}
		tileStack.push(starttile);
	}
}