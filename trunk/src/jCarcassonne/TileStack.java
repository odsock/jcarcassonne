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

public class TileStack extends Stack<Tile>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TileStack()
	{
		super();
	}

	public void shuffleStack()
	{
		Stack<Tile> tempStack = new Stack<Tile>();
		Tile startTile = null;

		//randomly push tiles from list onto stack
		while(!isEmpty())
		{
			Random rand = new Random();
			int r = rand.nextInt(this.size());
			Tile t = this.get(r);
			this.remove(r);
			
			if(t.name.equals("startTile.jpg"))
				startTile = t;
			else
				tempStack.push(t);
		}

		if(this.isEmpty()) {
			this.addAll(tempStack);
			this.push(startTile);
		}
	}

	public void loadTileSet(String tilesetFilename)
	{
		try{
			BufferedReader in = new BufferedReader(new FileReader(tilesetFilename));

			while(in.ready())
			{
				//read one tile description into the list
				ArrayList<String[]> tileDescription = new ArrayList<String[]>();

				String line = in.readLine();
				while(!line.isEmpty())
				{
					tileDescription.add(line.split(" "));
					line = in.readLine();
				}

				//create tiles
				int tileCount = Integer.parseInt(tileDescription.get(1)[1]);
				for(int i = 0; i < tileCount; i++)
				{
					//create tile
					Tile t = createTile(tileDescription);

					//add finished tile to stack
					this.push(t);
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Error reading " + tilesetFilename + ".");
			System.out.println(e);
			System.exit(0);
		}
	}

	private Tile createTile(ArrayList<String[]> tileDescription) {
		//create image and tile
		String imageFilename = tileDescription.get(0)[1];
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(imageFilename));
		}
		catch(IOException e) {
			System.out.println();
			System.out.println(e);
			System.exit(0);
		}
		Tile t = new Tile(img, imageFilename);

		//create and add tile features
		createTileFeatures(tileDescription, t);

		//check tile for null features
		String err = t.verifyFeatures();
		if(err != null)
			System.out.println(err);

		return t;
	}

	private void createTileFeatures(ArrayList<String[]> tileDescription, Tile t) throws NumberFormatException {
		for(int j = 2; j < tileDescription.size(); j++)
		{
			String[] fs = tileDescription.get(j);
			//instantiate feature based on feature type
			TileFeature f = new TileFeature(TileFeature.Feature.valueOf(fs[0]));

			//check for flag
			int k = 1;
			if(fs[1].equals("flag")){
				f.setFlag(true);
				k++;
			}

			//add feature to tile at borders
			for(; k < fs.length; k++)
			{
				t.addFeature(f, Integer.parseInt(fs[k]));
			}
		}
	}
}