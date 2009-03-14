package jCarcassonne;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;

public class TileStackReader {

	Stack<Tile> tileStack;

	public Stack<Tile> getStack()
	{
		return tileStack;
	}

	public void fillStack()
	{
		//fill list with tiles first
		ArrayList<Tile> templist = new ArrayList<Tile>();

		try{
			BufferedReader in = new BufferedReader(new FileReader("tileset.txt"));
			//borderCheck makes sure all borders of tile are covered by features
			while(in.ready())
			{
				//read image file name, then load image
				String filename = in.readLine().split(" ")[1];
				BufferedImage img = ImageIO.read(new File(filename));

				//read tile count
				int count = Integer.parseInt(in.readLine().split(" ")[1]);
				
				//create temp list of this tile
				ArrayList<Tile> tl = new ArrayList<Tile>();
				for(int i = 0; i < count; i++)
					tl.add(new Tile(img, filename));

				//read list of tile features
				String line = in.readLine();
				while(line != "")
				{
					String[] lineSplit = line.split(" ");

					//create the feature
					TileFeature.Feature featureType = TileFeature.Feature.valueOf(lineSplit[0]);

					//check for flag on this feature
					boolean flag = false;
					if(lineSplit[1].equals("flag")) {
						flag = true;
					}
					int i =  flag ? 2 : 1;

					//add feature to each tile in tl
					for(; i < lineSplit.length; i++)
					{
						int b = Integer.parseInt(lineSplit[i]);
						for(Tile t : tl)
							t.addFeature(new TileFeature(featureType), b);
					}
					
					//get next line for loop
					line = in.readLine();
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error reading tile set. \n" + e);
			System.exit(0);
		}
	}

	public void fillStackOld() {
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
				TileFeature.Feature[] features = new TileFeature.Feature[5];
				for(int i = 0; i < 5; i++){
					String t = in.readLine();
					String[] temp = t.split(" ");
					features[i] = TileFeature.Feature.valueOf(temp[1]);
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
}
