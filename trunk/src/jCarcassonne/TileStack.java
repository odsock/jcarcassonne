package jCarcassonne;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import jCarcassonne.TileFeature.FeatureEnum;

import javax.imageio.ImageIO;

public class TileStack extends Stack<Tile>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String imageFileExtension = ".gif";
	private int tileWidth = 128;
	private int tileHeight = 128;

	//read the tileset file, add the created tiles to the stack
	public void loadTileSet(String tilesetFilename)
	{
		try{
			BufferedReader in = new BufferedReader(new FileReader(tilesetFilename));

			int tilesRead = 1;
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
					
					//verify tile creation
					if(t == null)
						throw new Exception("Error at tile " + tilesRead);

					//add finished tile to stack
					this.push(t);
				}
				
				tilesRead++;
			}
		}
		catch(Exception e)
		{
			System.out.println("Error reading " + tilesetFilename + ".");
			System.out.println(e);
			System.exit(0);
		}
	}

	//helper method to parse tile description strings from the tileset file
	private Tile createTile(ArrayList<String[]> tileDescription) {
		String tileName = tileDescription.get(0)[1];
		String imageFilename = tileName + imageFileExtension;
		
		//read image
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(imageFilename));
		}
		catch(IOException e) {
			System.out.println();
			System.out.println(e);
			System.exit(0);
		}
		
		//return null if image is the wrong size
		if(img.getWidth() != tileWidth || img.getHeight() != tileHeight)
			return null;
		
		//create tile
		Tile t = new Tile(img, tileName);

		//create and add tile features
		createTileFeatures(tileDescription, t);

		//check tile for null features (shouldn't be any)
		String err = t.verifyFeatures();
		if(err != null)
			System.out.println(err);

		return t;
	}

	//parses tile feature string from the tileset file, creates features and adds them to tile
	private void createTileFeatures(ArrayList<String[]> tileDescription, Tile tile) throws NumberFormatException {
		TileFeatureFactory featureFactory = new TileFeatureFactory();
		
		for(int j = 2; j < tileDescription.size(); j++)
		{
			String[] featureString = tileDescription.get(j);
			
			//read feature type
			FeatureEnum featureType = FeatureEnum.valueOf(featureString[0]);
			
			//read token coordinates for this feature
			int tokenX = Integer.parseInt(featureString[1]);
			int tokenY = Integer.parseInt(featureString[2]);
			
			//parse border bit string
			String borderString = featureString[3];
			boolean[] borderArray = new boolean[13];
			for(int b = 0; b < borderArray.length; b++)
			{
				if(borderString.charAt(b) == '1')
					borderArray[b] = true;
				else
					borderArray[b] = false;
			}
			
			//check for any flags on the feature
			String flag = featureString.length > 4 ? featureString[4] : null;
			
			TileFeature feature = featureFactory.newTileFeature(featureType, tokenX, tokenY, tile, flag);
			
			//add feature to tile borders
			for(int k = 0; k < borderArray.length; k++)
			{
				if(borderArray[k])
					tile.addFeature(feature, k);
			}
		}
	}
	
	//randomizes the order of tiles in the stack
	public void shuffleStack()
	{
		Stack<Tile> tempStack = new Stack<Tile>();
		Tile startTile = null;

		//randomly push tiles onto stack
		while(!isEmpty())
		{
			Random rand = new Random();
			int r = rand.nextInt(this.size());
			Tile t = this.get(r);
			this.remove(r);
			
			//capture the first start tile
			if(t.name.equals("startTile") && startTile == null)
				startTile = t;
			else
				tempStack.push(t);
		}

		//add the shuffled tiles back onto the stack, startTile last
		if(this.isEmpty()) {
			this.addAll(tempStack);
			this.push(startTile);
		}
	}
	
	public int getTileWidth(){
		return tileWidth;
	}
	public int getTileHeight(){
		return tileHeight;
	}
}