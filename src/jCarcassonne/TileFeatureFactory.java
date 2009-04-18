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
			if(flag != null && flag[0].equals("pennant"))
				pennant = true;
			City cityFeature = new City(maxNeighbors, tokenX, tokenY, tile, pennant, colorCode);
			
			feature = cityFeature;
		}
		else if(featureType == FeatureEnum.farm)
		{
			int[] cityColorCodes = new int[0];
			if(flag != null)
			{
				cityColorCodes = new int[flag.length];
				for(int i = 0; i < flag.length; i++)
					cityColorCodes[i] = Integer.decode(flag[i]);
			}
			
			Farm farmFeature = new Farm(maxNeighbors, tokenX, tokenY, tile, colorCode, cityColorCodes);
			feature = farmFeature;
		}
		
		return feature;
	}
}