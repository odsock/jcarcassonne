package jCarcassonne;

import jCarcassonne.TileFeature.FeatureEnum;

public class TileFeatureFactory 
{
	protected TileFeature newTileFeature(FeatureEnum featureType, int maxNeighbors, int tokenX, int tokenY, Tile tile, int colorCode, String[] flag)
	{
		TileFeature feature = null;

		if(featureType == FeatureEnum.road)
		{
			Road roadFeature = new Road(maxNeighbors, tokenX, tokenY, tile, colorCode);
			feature = roadFeature;
		}
		else if(featureType == FeatureEnum.cloister)
			feature = new Cloister(maxNeighbors, tokenX, tokenY, tile, colorCode);
		else if(featureType == FeatureEnum.city)
		{
			boolean pennant = false;
			int[] farmColorCodes = new int[0];

			if(flag != null)
			{
				int numNeighbors;
				if(flag[flag.length-1].equals("pennant"))
				{
					pennant = true;
					numNeighbors = flag.length-1;
				}
				else
					numNeighbors = flag.length;
				
				farmColorCodes = new int[numNeighbors];

				for(int i = 0; i < farmColorCodes.length && i < flag.length; i++)
					farmColorCodes[i] = Integer.decode(flag[i]);
			}

			City cityFeature = new City(maxNeighbors, tokenX, tokenY, tile, colorCode, pennant, farmColorCodes);
			feature = cityFeature;
		}
		else if(featureType == FeatureEnum.farm)
		{
			Farm farmFeature = new Farm(maxNeighbors, tokenX, tokenY, tile, colorCode);
			feature = farmFeature;
		}

		return feature;
	}
}